package com.ruinscraft.punishments.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class PooledMySQLStorage extends MySQLStorage {

    private HikariConfig hikariConfig;
    private HikariDataSource hikariDataSource;

    public PooledMySQLStorage(String host, int port, String database, String username, char[] password) {
        super(host, port, database, username, password);

        hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s", host, port, database));
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(new String(password));
        hikariConfig.setPoolName("ruinscraft-punishments-pool");
        hikariConfig.setMaximumPoolSize(10);

        hikariDataSource = new HikariDataSource(hikariConfig);

        try {
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() {
        try {
            return hikariDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void close() {
        if (hikariDataSource != null) {
            hikariDataSource.close();
        }
    }
}
