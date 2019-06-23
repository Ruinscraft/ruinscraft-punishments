package com.ruinscraft.punishments.storage;

import com.ruinscraft.punishments.Punishment;
import com.ruinscraft.punishments.PunishmentEntry;
import com.ruinscraft.punishments.PunishmentType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

public class MySQLStorage implements SQLStorage {

    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final char[] password;

    private Connection connection; // do not use directly, use #getConnection()

    public MySQLStorage(String host, int port, String database, String username, char[] password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;

        try (PreparedStatement create = getConnection().prepareStatement(
                "CREATE TABLE IF NOT EXISTS "
                        + Table.PUNISHMENTS
                        + " (punishment_id INT, punishment_type VARCHAR(12), punisher VARCHAR(36), offender VARCHAR(36), duration BIGINT, reason VARCHAR(255));")) {
            create.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                final String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database;
                connection = DriverManager.getConnection(jdbcUrl, username, new String(password));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }


    @Override
    public Callable<Void> insert(PunishmentEntry entry) {
        return () -> {
            try (PreparedStatement insert = getConnection().prepareStatement("")) {
                insert.execute();
            }
            return null;
        };
    }

    @Override
    public Callable<Void> delete(int punishmentId) {
        return () -> {
            try (PreparedStatement delete = getConnection().prepareStatement("")) {
                delete.execute();
            }
            return null;
        };
    }

    @Override
    public Callable<List<PunishmentEntry>> query(String offender) {
        return null;
    }

    @Override
    public Callable<List<Punishment>> queryByType(String offender, PunishmentType type) {
        return null;
    }

}
