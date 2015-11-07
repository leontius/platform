package cn.elvea.core.persistence.mybatis;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

@Intercepts(@Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}))
public final class PageInterceptor implements Interceptor {
    private static Logger logger = LoggerFactory.getLogger(PageInterceptor.class);

    static final int MAPPED_STATEMENT_INDEX = 0;
    static final int PARAMETER_INDEX = 1;
    static final int ROWBOUNDS_INDEX = 2;
    static final int RESULT_HANDLER_INDEX = 3;

    @Override
    public void setProperties(Properties properties) {
    }

    @Override
    public Object plugin(Object target) {
        if (Executor.class.isAssignableFrom(target.getClass())) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 处理分页和总数统计
        PageHelper pageHelper = new PageHelper();
        return pageHelper.processPage(invocation);
    }
}
