package se.nbis.lega.qc.processors.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import se.nbis.lega.qc.processors.Processor;

import java.util.List;

@Slf4j
@Component
public class HTSProcessor implements Processor {

    private JdbcTemplate jdbcTemplate;

    @Override
    public void accept(String message) {
        log.info("Message consumed: " + message);
        List<String> tables = jdbcTemplate.queryForList("SELECT * FROM information_schema.tables WHERE table_schema = 'lega'", String.class);
        log.info(tables.toString());
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
