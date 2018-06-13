package se.nbis.lega.qc.processors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public abstract class AbstractProcessor implements Processor {

    @Value("${lega.qc.db.schema}")
    private String schema;

    protected JdbcTemplate jdbcTemplate;

    @PostConstruct
    protected void init() {
        jdbcTemplate.execute(String.format("CREATE TABLE IF NOT EXISTS \"%s\".\"%s\"()", schema, getName()));
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
