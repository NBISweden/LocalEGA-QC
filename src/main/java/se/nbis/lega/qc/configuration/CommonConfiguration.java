package se.nbis.lega.qc.configuration;

import no.ifi.uio.crypt4gh.factory.HeaderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfiguration {

    @Bean
    public HeaderFactory headerFactory() {
        return HeaderFactory.getInstance();
    }

}
