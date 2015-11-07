package cn.elvea.repository;

import cn.elvea.core.persistence.mybatis.SqlProvider;
import org.apache.ibatis.annotations.SelectProvider;

import java.sql.Timestamp;

public interface BaseRepository {
    @SelectProvider(type = SqlProvider.class, method = "getTimeSql")
    Timestamp getTime();

    @SelectProvider(type = SqlProvider.class, method = "getDateSql")
    Timestamp getDate();
}
