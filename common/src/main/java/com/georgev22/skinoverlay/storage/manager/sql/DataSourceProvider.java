package com.georgev22.skinoverlay.storage.manager.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DataSourceProvider {

    private static HikariDataSource dataSource;

    public static void initializeForMySQL(String host, int port, String database, String user, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mariadb://" + host + ":" + port + "/" + database + "?useSSL=false&characterEncoding=utf8");
        config.setUsername(user);
        config.setPassword(password);

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setConnectionTimeout(10000);
        config.setPoolName("ProjectsMySQLPool");

        dataSource = new HikariDataSource(config);
    }

    public static void initializeForSQLite(String filePath) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + filePath);
        config.setConnectionTestQuery("SELECT 1");
        config.setMaximumPoolSize(1);
        config.setPoolName("ProjectsSQLitePool");

        dataSource = new HikariDataSource(config);
    }

    public static DataSource getDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not initialized");
        }
        return dataSource;
    }

    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
