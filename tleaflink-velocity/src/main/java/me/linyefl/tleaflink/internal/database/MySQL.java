package me.linyefl.tleaflink.internal.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.linyefl.tleaflink.TLeafLink;
import me.linyefl.tleaflink.internal.DbConfig;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQL implements Database{

    private static HikariDataSource hds; // MySQL
    @Override
    public void initialize() {
        String driver = null;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            driver = "com.mysql.cj.jdbc.Driver";
        } catch (ClassNotFoundException ignored) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                driver = "com.mysql.jdbc.Driver";
            } catch (ClassNotFoundException ignored1) {
                TLeafLink.INSTANCE.getLogger().info("无法找到数据库驱动");
            }
        }

        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driver);
        config.setPoolName("TLeafLink-MySQL");
        config.setJdbcUrl("jdbc:mysql://" + DbConfig.settings.mysql.host +":"+ DbConfig.settings.mysql.port + "/" + DbConfig.settings.mysql.database + DbConfig.settings.mysql.parameters);
        config.setUsername(DbConfig.settings.mysql.user);
        config.setPassword(DbConfig.settings.mysql.password);
        config.setConnectionTimeout(DbConfig.settings.pool.connectionTimeout);
        config.setIdleTimeout(DbConfig.settings.pool.idleTimeout);
        config.setMaxLifetime(DbConfig.settings.pool.maxLifetime);
        config.setMaximumPoolSize(DbConfig.settings.pool.maximumPoolSize);
        config.setKeepaliveTime(DbConfig.settings.pool.keepaliveTime);
        config.setMinimumIdle(DbConfig.settings.pool.minimumIdle);
        config.addDataSourceProperty("cachePrepStmts", "true" );
        config.addDataSourceProperty("prepStmtCacheSize", "250" );
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048" );

        hds = new HikariDataSource(config);
    }

    @Override
    public void close() {
        hds.close();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return hds.getConnection();
    }
}
