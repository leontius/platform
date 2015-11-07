package cn.elvea.config;

import cn.elvea.Application;
import cn.elvea.core.datasource.DynamicDataSource;
import cn.elvea.core.persistence.mybatis.PageInterceptor;
import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.util.*;

@Configuration
@PropertySource("classpath:application.properties")
@MapperScan(basePackages = "cn.elvea.repository")
public class Config implements TransactionManagementConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    @Autowired
    Environment env;

    @Bean(name = "dataSource")
    public DynamicDataSource dataSource() {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(Application.MASTER_DATASOURCE_PREFIX, masterDataSource());
        targetDataSources.put(Application.SLAVE_DATASOURCE_PREFIX, slaveDataSource());

        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.setDefaultTargetDataSource(slaveDataSource());

        return dynamicDataSource;
    }

    @Bean(name = "masterDataSource")
    public DataSource masterDataSource() {
        return createDataSource(Application.MASTER_DATASOURCE_PREFIX);
    }

    @Bean(name = "slaveDataSource")
    public DataSource slaveDataSource() {
        return createDataSource(Application.SLAVE_DATASOURCE_PREFIX);
    }

    private DataSource createDataSource(String prefix) {
        // 是否使用数据源
        boolean useJndi = env.getProperty(prefix + "." + "datasource.use-jndi", Boolean.class, false);
        // 数据源名称
        String jndiName = env.getProperty(prefix + "." + "datasource.jndi-name", "");
        // 数据库链接
        String url = env.getProperty(prefix + "." + "datasource.url", "");
        String username = env.getProperty(prefix + "." + "datasource.username", "");
        String password = env.getProperty(prefix + "." + "datasource.password", "");
        String driverClass = env.getProperty(prefix + "." + "datasource.driver-class", "");
        // 数据源默认初始链接数
        int initialSize = env.getProperty(prefix + "." + "datasource.initial-size", Integer.class, Application.DEFAULT_DATASOURCE_INIT_SIZE);
        // 数据源最大连接数
        int maxActive = env.getProperty(prefix + "." + "datasource.max-active", Integer.class, Application.DEFAULT_DATASOURCE_MAX_ACTIVE);
        // 数据源最小连接数
        int minIdle = env.getProperty(prefix + "." + "datasource.min-idle", Integer.class, Application.DEFAULT_DATASOURCE_MIN_IDLE);
        // 配置获取连接等待超时的时间
        int maxWait = env.getProperty(prefix + "." + "datasource.max-wait", Integer.class, Application.DEFAULT_DATASOURCE_MAX_WAIT);

        //
        if (useJndi) {
            try {
                logger.debug("get datasource from jndi - [{}].", jndiName);
                Context context = new InitialContext();
                DataSource dataSource = (DataSource) context.lookup(jndiName);
                return dataSource;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            logger.debug("create druid datasource.");
            logger.debug("url - {}.", url);
            logger.debug("username - {}.", username);
            logger.debug("password - {}.", password);
            logger.debug("driverClass - {}.", driverClass);
            logger.debug("initialSize - {}.", initialSize);
            logger.debug("maxActive - {}.", maxActive);
            logger.debug("minIdle - {}.", minIdle);

            try {
                DruidDataSource datasource = new DruidDataSource();
                datasource.setUrl(url);
                datasource.setDriverClassName(driverClass);
                datasource.setUsername(username);
                datasource.setPassword(password);
                datasource.setInitialSize(initialSize);
                datasource.setMaxActive(maxActive);
                datasource.setMinIdle(minIdle);
                datasource.setMaxWait(maxWait);
                datasource.setFilters("stat,slf4j");
                datasource.setProxyFilters(getDruidFilters());
                return datasource;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    public List<Filter> getDruidFilters() {
        Slf4jLogFilter slf4jLogFilter = new Slf4jLogFilter();
        slf4jLogFilter.setDataSourceLogEnabled(false);
        slf4jLogFilter.setStatementLogEnabled(false);
        slf4jLogFilter.setStatementExecutableSqlLogEnable(true);
        slf4jLogFilter.setResultSetLogEnabled(false);
        slf4jLogFilter.setResultSetCloseAfterLogEnabled(false);
        slf4jLogFilter.setConnectionLogEnabled(false);

        List<Filter> filters = new ArrayList<>();
        filters.add(new StatFilter());
        filters.add(slf4jLogFilter);
        return filters;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return transactionManager();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        DataSourceTransactionManager txManager = new DataSourceTransactionManager();
        txManager.setDataSource(dataSource());
        return txManager;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sf = new SqlSessionFactoryBean();
        sf.setDataSource(dataSource());
        sf.setTypeAliasesPackage("cn.elvea.domain");
        sf.setPlugins(getMyBatisPlugins());
        sf.setConfigurationProperties(new Properties());
        return sf.getObject();
    }

    private Interceptor[] getMyBatisPlugins() {
        Interceptor[] interceptors = new Interceptor[1];
        interceptors[0] = new PageInterceptor();
        return interceptors;
    }
}
