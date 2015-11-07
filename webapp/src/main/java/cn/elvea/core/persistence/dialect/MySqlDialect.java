package cn.elvea.core.persistence.dialect;

public class MySqlDialect extends Dialect {
    @Override
    public String getTimeSql() {
        return " select now() as t ";
    }

    @Override
    public String getDateSql() {
        return " select now() as t ";
    }

    @Override
    public String getLimitSql(String sql, int offset, int limit) {
        return sql + " limit " + offset + "," + limit;
    }

    @Override
    public String getCountString(String sql) {
        return "select count(1) from (" + sql + ") tmp_count";
    }
}
