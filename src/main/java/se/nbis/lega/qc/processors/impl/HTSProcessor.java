package se.nbis.lega.qc.processors.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import se.nbis.lega.qc.processors.Processor;

@Slf4j
@Component
public class HTSProcessor implements Processor {

    @Override
    public void accept(String message) {
        log.info("Message consumed: " + message);
    }

}
