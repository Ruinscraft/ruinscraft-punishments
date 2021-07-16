package com.ruinscraft.punishments.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class PooledMySQLPunishmentStorage extends MySQLPunishmentStorage {

    private HikariConfig hikariConfig;
    private HikariDataSource hikariDataSource;

    public PooledMySQLPunishmentStorage(String host, int port, String database, String username, String password) {
        hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s?useSSL=false", host, port, database));
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
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
