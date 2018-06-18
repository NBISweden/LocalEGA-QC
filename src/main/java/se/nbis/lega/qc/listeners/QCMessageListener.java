package se.nbis.lega.qc.listeners;

import com.google.gson.Gson;
import htsjdk.samtools.seekablestream.SeekableHTTPStream;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import no.ifi.uio.crypt4gh.factory.HeaderFactory;
import no.ifi.uio.crypt4gh.pojo.Header;
import no.ifi.uio.crypt4gh.stream.Crypt4GHInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import se.nbis.lega.qc.pojo.FileDescriptor;
import se.nbis.lega.qc.pojo.FileStatus;
import se.nbis.lega.qc.processors.Processor;

import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Collection;

import static se.nbis.lega.qc.pojo.FileStatus.COMPLETED;
import static se.nbis.lega.qc.pojo.FileStatus.ERROR;

@Slf4j
@Component
public class QCMessageListener implements MessageListener {

    private String keysEndpoint;
    private String passphrase;
    private String bucket;

    private Gson gson;
    private HeaderFactory headerFactory;
    private MinioClient s3Client;
    private JdbcTemplate jdbcTemplate;

    private Collection<Processor> processors;

    @Override
    public void onMessage(Message message) {
        try {
            FileDescriptor fileDescriptor = gson.fromJson(new String(message.getBody()), FileDescriptor.class);
            byte[] headerBytes = Hex.decode(fileDescriptor.getHeader());
            URL url = new URL(keysEndpoint + fileDescriptor.getKeyId());
            URLConnection urlConnection = url.openConnection();
            urlConnection.setRequestProperty(HttpHeaders.CONTENT_TYPE, ContentType.TEXT_PLAIN.toString());
            String key = IOUtils.toString(urlConnection.getInputStream(), Charset.defaultCharset());
            Header header = headerFactory.getHeader(headerBytes, key, passphrase);
            String id = fileDescriptor.getId();
            String fileURL = s3Client.presignedGetObject(bucket, id);
            FileStatus fileStatus = COMPLETED;
            for (Processor processor : processors) {
                if (!processor.apply(new Crypt4GHInputStream(new SeekableHTTPStream(new URL(fileURL)), header))) {
                    fileStatus = ERROR;
                    break;
                }
            }
            jdbcTemplate.update("UPDATE files SET status = '?' WHERE id = ?", fileStatus, Long.parseLong(id));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Value("${lega.keys.endpoint}")
    public void setKeysEndpoint(String keysEndpoint) {
        this.keysEndpoint = keysEndpoint;
    }

    @Value("${lega.keys.passphrase}")
    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    @Value("${lega.s3.bucket}")
    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    @Autowired
    public void setGson(Gson gson) {
        this.gson = gson;
    }

    @Autowired
    public void setHeaderFactory(HeaderFactory headerFactory) {
        this.headerFactory = headerFactory;
    }

    @Autowired
    public void setS3Client(MinioClient s3Client) {
        this.s3Client = s3Client;
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    public void setProcessors(Collection<Processor> processors) {
        this.processors = processors;
    }

}
