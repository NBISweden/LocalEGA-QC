package se.nbis.lega.qc.listeners;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import htsjdk.samtools.seekablestream.SeekableFileStream;
import htsjdk.samtools.seekablestream.SeekableHTTPStream;
import htsjdk.samtools.seekablestream.SeekableStream;
import io.minio.MinioClient;
import io.minio.errors.*;
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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.xmlpull.v1.XmlPullParserException;
import se.nbis.lega.qc.pojo.*;
import se.nbis.lega.qc.processors.Processor;

import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

import static se.nbis.lega.qc.pojo.FileStatus.COMPLETED;
import static se.nbis.lega.qc.pojo.FileStatus.ERROR;

@Slf4j
@Component
public class QCMessageListener implements MessageListener {

    private String storageDriver;
    private String keysEndpoint;
    private String passphrase;
    private String bucket;
    private String exchange;
    private String routingKey;

    private Gson gson;
    private HeaderFactory headerFactory;
    private MinioClient s3Client;
    private JdbcTemplate jdbcTemplate;
    private RabbitTemplate rabbitTemplate;

    private Collection<Processor> processors;

    @Override
    public void onMessage(Message message) {
        try {
            FileDescriptor fileDescriptor = gson.fromJson(new String(message.getBody()), FileDescriptor.class);
            String headerString = jdbcTemplate.queryForObject("SELECT header FROM files WHERE id = ?", String.class, fileDescriptor.getId());
            byte[] headerBytes = Hex.decode(headerString);
            String keyId = headerFactory.getKeyId(headerBytes);
            URL url = new URL(String.format(keysEndpoint, keyId));
            URLConnection urlConnection = url.openConnection();
            urlConnection.setRequestProperty(HttpHeaders.CONTENT_TYPE, ContentType.TEXT_PLAIN.toString());
            String key = IOUtils.toString(urlConnection.getInputStream(), Charset.defaultCharset());
            Header header = headerFactory.getHeader(headerBytes, key, passphrase);
            FileStatus fileStatus = COMPLETED;
            for (Processor processor : processors) {
                if (!processor.apply(getCrypt4GHStream(header, fileDescriptor))) {
                    fileStatus = ERROR;
                    break;
                }
            }
            makeRecords(fileDescriptor, fileStatus);
        } catch (JsonSyntaxException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            FileDescriptor fileDescriptor = gson.fromJson(new String(message.getBody()), FileDescriptor.class);
            makeRecords(fileDescriptor, FileStatus.ERROR);
        }
    }

    private Crypt4GHInputStream getCrypt4GHStream(Header header, FileDescriptor fileDescriptor) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, InsufficientDataException, NoResponseException, XmlPullParserException, InternalException, ErrorResponseException, InvalidExpiresRangeException, InvalidBucketNameException {
        StorageDriver driver = StorageDriver.getValue(storageDriver);
        SeekableStream seekableStream;
        switch (driver) {
            case FILESYSTEM:
                seekableStream = new SeekableFileStream(new File(fileDescriptor.getFilePath()));
                break;
            default:
                String fileURL = s3Client.presignedGetObject(bucket, String.valueOf(fileDescriptor.getId()));
                seekableStream = new SeekableHTTPStream(new URL(fileURL));
                break;
        }
        return new Crypt4GHInputStream(seekableStream, false, header);
    }

    private void makeRecords(FileDescriptor fileDescriptor, FileStatus fileStatus) {
        OriginalMessage originalMessage = fileDescriptor.getOriginalMessage();
        originalMessage.setStatus(new Status(fileStatus.getStatus().toUpperCase(), fileDescriptor.getStableId()));
        rabbitTemplate.convertAndSend(exchange, routingKey, gson.toJson(originalMessage));
        jdbcTemplate.update("UPDATE files SET status = ?::status WHERE id = ?", fileStatus.getStatus(), fileDescriptor.getId());
    }

    @Value("${lega.storage.driver}")
    public void setStorageDriver(String storageDriver) {
        this.storageDriver = storageDriver;
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

    @Value("${lega.qc.mq.exchange}")
    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    @Value("${lega.qc.mq.routing-key}")
    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
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
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Autowired
    public void setProcessors(Collection<Processor> processors) {
        this.processors = processors;
    }

}
