package cn.elvea.core.persistence.dialect;

import cn.elvea.core.persistence.jdbc.JdbcUtils;
import cn.elvea.exception.DatabaseException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class Dialect {
    private static Dialect instance = null;

    public static Dialect getInstance() throws DatabaseException {
        return instance;
    }

    public static Dialect getInstance(DataSource ds) throws DatabaseException {
        if (instance != null) {
            return instance;
        }

        Connection con = null;
        try {
            con = ds.getConnection();

            instance = getInstance(con);
        } catch (Exception ex) {
            throw new DatabaseException("Could not get dialect instance.", ex);
        } finally {
            JdbcUtils.close(con);
        }
        return instance;
    }

    public static Dialect getInstance(Connection con) throws DatabaseException {
        String dialectName = null;
        try {
            dialectName = getDialectName(con.getMetaData().getDatabaseProductName().toLowerCase());

            return getInstance(dialectName);
        } catch (SQLException e) {
            throw new DatabaseException("Could not get instance for dialect class: " + dialectName, e);
        }
    }

    private static Dialect getInstance(String dialectName) throws DatabaseException {
        try {
            return (Dialect) Class.forName(dialectName).newInstance();
        } catch (ClassNotFoundException cnfe) {
            throw new DatabaseException("Dialect class not found: " + dialectName);
        } catch (Exception e) {
            throw new DatabaseException("Could not get instance for dialect class: " + dialectName, e);
        }
    }

    private static String getDialectName(String databaseProductName) throws DatabaseException {
        if (databaseProductName.contains("mysql")) {
            return MySqlDialect.class.getName();
        } else {
            throw new DatabaseException("Unsuport Datebase.");
        }
    }

    public abstract String getTimeSql();

    public abstract String getDateSql();

    public abstract String getLimitSql(String sql, int offset, int limit);

    public abstract String getCountString(String sql);
}
