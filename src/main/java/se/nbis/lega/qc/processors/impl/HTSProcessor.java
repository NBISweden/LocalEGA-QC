package se.nbis.lega.qc.processors.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import se.nbis.lega.qc.pojo.QCMessage;
import se.nbis.lega.qc.processors.AbstractProcessor;

import java.sql.ResultSetMetaData;

@Slf4j
@Component
public class HTSProcessor extends AbstractProcessor {

    @Override
    public void accept(QCMessage message) {
        log.info("Message consumed: " + message);
        try {
            jdbcTemplate.query("SELECT * FROM files", (RowMapper<Void>) (resultSet, i) -> {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int j = 1; j <= columnCount; j++) {
                    String columnName = metaData.getColumnName(j);
                    System.out.print(columnName + "|");
                }
                System.out.println();
                while (resultSet.next()) {
                    for (int j = 1; j <= columnCount; j++) {
                        System.out.print(resultSet.getString(j) + "|");
                    }
                    System.out.println();
                }
                System.out.println();
                return null;
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public String getName() {
        return "hts";
    }

}
