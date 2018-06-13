package se.nbis.lega.qc.listeners;

import com.google.gson.Gson;
import htsjdk.samtools.seekablestream.SeekableHTTPStream;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import no.ifi.uio.crypt4gh.factory.HeaderFactory;
import no.ifi.uio.crypt4gh.pojo.Header;
import no.ifi.uio.crypt4gh.stream.Crypt4GHInputStream;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.nbis.lega.qc.pojo.FileDescriptor;
import se.nbis.lega.qc.processors.Processor;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;

@Slf4j
@Component
public class QCMessageListener implements MessageListener {

    private String keysEndpoint;
    private String passphrase;
    private String bucket;

    private Gson gson;
    private HeaderFactory headerFactory;
    private MinioClient s3Client;

    private Collection<Processor> processors;

    @Override
    public void onMessage(Message message) {
        try {
            FileDescriptor fileDescriptor = gson.fromJson(new String(message.getBody()), FileDescriptor.class);
            byte[] headerBytes = Hex.decode(fileDescriptor.getHeader());
            String key = IOUtils.toString(new URL(keysEndpoint + fileDescriptor.getKeyId()).openStream(), Charset.defaultCharset());
            Header header = headerFactory.getHeader(headerBytes, key, passphrase);
            String fileURL = s3Client.presignedGetObject(bucket, fileDescriptor.getId());
            for (Processor processor : processors) {
                processor.apply(new Crypt4GHInputStream(new SeekableHTTPStream(new URL(fileURL)), header));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Value("lega.keys.endpoint")
    public void setKeysEndpoint(String keysEndpoint) {
        this.keysEndpoint = keysEndpoint;
    }

    @Value("lega.keys.passphrase")
    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    @Value("lega.s3.bucket")
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
    public void setProcessors(Collection<Processor> processors) {
        this.processors = processors;
    }

}
