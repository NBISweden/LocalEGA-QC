package se.nbis.lega.qc.configuration;

import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import no.ifi.uio.crypt4gh.factory.HeaderFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfiguration {

    @Bean
    public HeaderFactory headerFactory() {
        return HeaderFactory.getInstance();
    }

}
