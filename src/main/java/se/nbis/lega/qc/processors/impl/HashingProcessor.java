package se.nbis.lega.qc.processors.impl;

import lombok.extern.slf4j.Slf4j;
import no.ifi.uio.crypt4gh.stream.Crypt4GHInputStream;
import org.springframework.stereotype.Component;
import se.nbis.lega.qc.processors.AbstractProcessor;

@Slf4j
@Component
public class HashingProcessor extends AbstractProcessor {

    @Override
    public Boolean apply(Crypt4GHInputStream crypt4GHInputStream) {
        return true;
    }

}
