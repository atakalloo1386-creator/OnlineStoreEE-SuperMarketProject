package com.mftplus.mftonlinestoreee.integration.tomee;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.openejb.resource.jdbc.pool.PoolDataSourceCreator;

import javax.sql.CommonDataSource;
import javax.sql.DataSource;
import java.util.Properties;

public class HikariDataSourceCreator extends PoolDataSourceCreator {

    @Override
    public DataSource pool(String name, DataSource dataSource, Properties properties) {
        HikariDataSource pool = build(HikariDataSource.class, properties);
        pool.setPoolName(name);
        pool.setDataSource(dataSource);
        return pool;
    }

    @Override
    public CommonDataSource pool(String name, String driver, Properties properties) {
        HikariDataSource pool = build(HikariDataSource.class, properties);
        pool.setPoolName(name);
        pool.setDriverClassName(driver);
        return pool;
    }

    @Override
    protected void doDestroy(CommonDataSource dataSource) {
        ((HikariDataSource) dataSource).close();
    }
}
