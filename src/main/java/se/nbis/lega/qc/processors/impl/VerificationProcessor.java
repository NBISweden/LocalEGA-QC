package se.nbis.lega.qc.processors.impl;

import lombok.extern.slf4j.Slf4j;
import no.ifi.uio.crypt4gh.stream.Crypt4GHInputStream;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import se.nbis.lega.qc.processors.AbstractProcessor;

@Slf4j
@Component
public class VerificationProcessor extends AbstractProcessor {

    @Override
    public Boolean apply(Crypt4GHInputStream crypt4GHInputStream) {
        try {
            while (IOUtils.read(crypt4GHInputStream, new byte[1024]) <= 0) {
                // omitting the results
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

}
