package se.nbis.lega.qc.configuration;

import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Configuration {

    private String minioEndpoint;
    private String minioAccessKey;
    private String minioSecretKey;

    @Bean
    public MinioClient s3Client() throws InvalidPortException, InvalidEndpointException {
        return new MinioClient(minioEndpoint, minioAccessKey, minioSecretKey);
    }

    @Value("${lega.s3.endpoint}")
    public void setMinioEndpoint(String minioEndpoint) {
        this.minioEndpoint = minioEndpoint;
    }

    @Value("${lega.s3.access-key}")
    public void setMinioAccessKey(String minioAccessKey) {
        this.minioAccessKey = minioAccessKey;
    }

    @Value("${lega.s3.secret-key}")
    public void setMinioSecretKey(String minioSecretKey) {
        this.minioSecretKey = minioSecretKey;
    }

}
