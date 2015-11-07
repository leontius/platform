package cn.elvea.core.dao;

import cn.elvea.core.persistence.dialect.Dialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Spring JDBC Dao
 */
@Service
public class BaseDao {
    protected DataSource dataSource;

    protected JdbcTemplate jdbcTemplate;

    protected Dialect dialect;

    @Autowired
    public void init(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);

        this.dialect = Dialect.getInstance(dataSource);
    }

    public Timestamp getTime() {
        return jdbcTemplate.queryForObject(dialect.getTimeSql(), Timestamp.class);
    }

    public Date getDate() {
        return jdbcTemplate.queryForObject(dialect.getDateSql(), Date.class);
    }
}
