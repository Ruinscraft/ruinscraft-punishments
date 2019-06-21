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

        try (PreparedStatement create = getConnection().prepareStatement("")) { // TODO:

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(""); // TODO:
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }


    @Override
    public Callable<Void> insert(PunishmentEntry entry) {
        return null;
    }

    @Override
    public Callable<Void> delete(int punishmentId) {
        return null;
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
