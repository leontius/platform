package cn.elvea.core.persistence.mybatis;

import cn.elvea.core.persistence.dialect.Dialect;
import cn.elvea.core.persistence.jdbc.JdbcUtils;
import cn.elvea.utils.Page;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class PageHelper {
    private static Logger logger = LoggerFactory.getLogger(PageHelper.class);

    public Object processPage(Invocation invocation) throws Exception {
        final Object[] queryArgs = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) queryArgs[PageInterceptor.MAPPED_STATEMENT_INDEX];
        Object parameterObject = queryArgs[PageInterceptor.PARAMETER_INDEX];

        BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
        String sql = boundSql.getSql().trim().replaceAll(";$", "");

        // 从参数列表中检查是否存在分页的对象,有则做分页查询处理
        Page<?> page = findPageObject(parameterObject);
        if (page != null) {
            try {
                // 获取数据源
                DataSource dataSource = mappedStatement.getConfiguration().getEnvironment().getDataSource();
                // 获取数据库字典
                Dialect dialect = Dialect.getInstance(dataSource);

                // 是否要统计总记录数
                if (page.isCountable()) {
                    // 从数据库字典生成记录数统计的SQL
                    String countSql = dialect.getCountString(sql);

                    page.setTotal(queryTotal(dataSource, countSql, mappedStatement, boundSql));
                }

                // 生成特定数据库的分页SQL
                String limitSql = dialect.getLimitSql(sql, page.getStart(), page.getLimit());

                queryArgs[PageInterceptor.ROWBOUNDS_INDEX] = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
                queryArgs[PageInterceptor.MAPPED_STATEMENT_INDEX] = copyFromNewSql(mappedStatement, boundSql, limitSql);

                Object ret = invocation.proceed();

//                page.setRows((List) ret);

                return ret;
            } catch (SQLException e) {
                logger.error("paging error", e);
            }
        }
        return invocation.proceed();
    }

    private int queryTotal(DataSource dataSource, String sql, MappedStatement mappedStatement, BoundSql boundSql) throws SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            stmt = con.prepareStatement(sql);

            // 调用MyBatis设置SQL参数
            BoundSql countBoundSql = new BoundSql(mappedStatement.getConfiguration(), sql, boundSql.getParameterMappings(), boundSql.getParameterObject());
            setParameters(stmt, mappedStatement, countBoundSql, boundSql.getParameterObject());

            rs = stmt.executeQuery();
            int totalCount = 0;
            if (rs.next()) {
                totalCount = rs.getInt(1);
            }
            return totalCount;
        } catch (SQLException e) {
            throw e;
        } finally {
            JdbcUtils.close(rs, stmt);
            JdbcUtils.close(con);
        }
    }

    private void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql, Object parameterObject) throws SQLException {
        ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
        parameterHandler.setParameters(ps);
    }

    private Page findPageObject(Object params) {
        if (params == null) {
            return null;
        }

        if (Page.class.isAssignableFrom(params.getClass())) { // 单个参数 表现为参数对象
            return (Page) params;
        } else if (params instanceof ParamMap) { // 多个参数 表现为 ParamMap
            ParamMap<Object> paramMap = (ParamMap) params;
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                Object paramValue = entry.getValue();
                if (paramValue != null && Page.class.isAssignableFrom(paramValue.getClass())) {
                    return (Page) paramValue;
                }
            }
        }
        return null;
    }

    private MappedStatement copyFromNewSql(MappedStatement ms, BoundSql boundSql, String sql) {
        BoundSql newBoundSql = copyFromBoundSql(ms, boundSql, sql);
        return copyFromMappedStatement(ms, new BoundSqlSqlSource(newBoundSql));
    }

    public static class BoundSqlSqlSource implements SqlSource {
        BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }

    private BoundSql copyFromBoundSql(MappedStatement ms, BoundSql boundSql, String sql) {
        BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql, boundSql.getParameterMappings(), boundSql.getParameterObject());
        for (ParameterMapping mapping : boundSql.getParameterMappings()) {
            String prop = mapping.getProperty();
            if (boundSql.hasAdditionalParameter(prop)) {
                newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
            }
        }
        return newBoundSql;
    }

    private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());

        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuffer keyProperties = new StringBuffer();
            for (String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }

        //setStatementTimeout()
        builder.timeout(ms.getTimeout());

        //setStatementResultMap()
        builder.parameterMap(ms.getParameterMap());

        //setStatementResultMap()
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());

        //setStatementCache()
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());

        return builder.build();
    }
}
