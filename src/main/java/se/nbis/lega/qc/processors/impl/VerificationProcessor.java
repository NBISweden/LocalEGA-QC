package se.nbis.lega.qc.processors.impl;

import lombok.extern.slf4j.Slf4j;
import no.ifi.uio.crypt4gh.stream.Crypt4GHInputStream;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;
import se.nbis.lega.qc.processors.AbstractProcessor;

import java.util.Arrays;

@Slf4j
@Component
public class VerificationProcessor extends AbstractProcessor {

    @Override
    public Boolean apply(Crypt4GHInputStream crypt4GHInputStream) {
        try {
            byte[] calculatedDigest = DigestUtils.sha256(crypt4GHInputStream);
            log.debug("Calculated digest: " + Arrays.toString(calculatedDigest));
            byte[] embeddedDigest = crypt4GHInputStream.getDigest();
            log.debug("Embedded digest: " + Arrays.toString(embeddedDigest));
            return Arrays.equals(calculatedDigest, embeddedDigest);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

}
