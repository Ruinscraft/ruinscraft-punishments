package com.ruinscraft.punishments.storage;

import com.ruinscraft.punishments.Punishment;
import com.ruinscraft.punishments.PunishmentEntry;
import com.ruinscraft.punishments.PunishmentType;
import com.ruinscraft.punishments.offender.Offender;
import com.ruinscraft.punishments.offender.UUIDOffender;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

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

        try (PreparedStatement create_punishments = getConnection().prepareStatement(
                "CREATE TABLE IF NOT EXISTS " +
                        Table.PUNISHMENTS +
                        " (punishment_id INT NOT NULL AUTO_INCREMENT, " +
                        "server_context VARCHAR(64) DEFAULT 'primary', " +
                        "punishment_type VARCHAR(12), " +
                        "punisher VARCHAR(36), " +
                        "punisher_username VARCHAR(16), " +
                        "offender VARCHAR(36), " +
                        "offender_username VARCHAR(16), " +
                        "inception_time BIGINT, " +
                        "expiration_time BIGINT, " +
                        "reason VARCHAR(255)," +
                        "PRIMARY KEY (punishment_id));")) {
            create_punishments.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (PreparedStatement create_addresses = getConnection().prepareStatement(
                "CREATE TABLE IF NOT EXISTS " +
                        Table.ADDRESSES +
                        " (user VARCHAR(36) NOT NULL, " +
                        "address VARCHAR(127) NOT NULL);")) {
            create_addresses.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                final String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false";
                connection = DriverManager.getConnection(jdbcUrl, username, new String(password));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }

    @Override
    public CompletableFuture<Void> insert(PunishmentEntry entry) {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement insert = getConnection().prepareStatement(
                    "INSERT INTO " + Table.PUNISHMENTS +
                            " (server_context, punishment_type, punisher, punisher_username, offender, offender_username, inception_time, expiration_time, reason)" +
                            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
                insert.setString(1, entry.punishment.getServerContext());
                insert.setString(2, entry.type.name());
                insert.setString(3, entry.punishment.getPunisher().toString());
                insert.setString(4, entry.punishment.getPunisherUsername());
                insert.setString(5, entry.punishment.getOffender().getIdentifier().toString());
                insert.setString(6, entry.punishment.getOffenderUsername());
                insert.setLong(7, entry.punishment.getInceptionTime());
                insert.setLong(8, entry.punishment.getExpirationTime());
                insert.setString(9, entry.punishment.getReason());
                insert.execute();
                try (ResultSet rs = insert.getGeneratedKeys()) {
                    while (rs.next()) {
                        final int punishmentId = rs.getInt(1);
                        entry.punishment.setPunishmentId(punishmentId);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        });
    }

    @Override
    public CompletableFuture<Void> update(PunishmentEntry entry) {
        return CompletableFuture.supplyAsync(() -> {
            if (entry.punishment.getPunishmentId() == 0) {
                return null; // punishment not in database, cannot update
            }

            try (PreparedStatement update = getConnection().prepareStatement(
                    "UPDATE " + Table.PUNISHMENTS + " SET expiration_time = ?, reason = ? WHERE punishment_id = ?;")) {
                update.setLong(1, entry.punishment.getExpirationTime());
                update.setString(2, entry.punishment.getReason());
                update.setInt(3, entry.punishment.getPunishmentId());
                update.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        });
    }

    @Override
    public CompletableFuture<Void> delete(int punishmentId) {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement delete = getConnection().prepareStatement(
                    "DELETE FROM " + Table.PUNISHMENTS + " WHERE punishment_id = ?;")) {
                delete.setInt(1, punishmentId);
                delete.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        });
    }

    @Override
    public CompletableFuture<List<PunishmentEntry>> queryOffender(Offender offender) {
        return CompletableFuture.supplyAsync(() -> {
            List<PunishmentEntry> entries = new ArrayList<>();

            try (PreparedStatement query = getConnection().prepareStatement(
                    "SELECT * FROM " + Table.PUNISHMENTS + " WHERE offender = ?;")) {
                query.setString(1, offender.toString());

                try (ResultSet rs = query.executeQuery()) {
                    while (rs.next()) {
                        int punishmentId = rs.getInt("punishment_id");
                        String serverContext = rs.getString("server_context");
                        PunishmentType type = PunishmentType.valueOf(rs.getString("punishment_type"));
                        UUID punisher = UUID.fromString(rs.getString("punisher"));
                        String punisherUsername = rs.getString("punisher_username");
                        long inceptionTime = rs.getLong("inception_time");
                        long expirationTime = rs.getLong("expiration_time");
                        String reason = rs.getString("reason");
                        Punishment punishment = Punishment.builder(punishmentId)
                                .serverContext(serverContext)
                                .punisher(punisher)
                                .punisherUsername(punisherUsername)
                                .offender(offender) // TODO: should set offenderUsername also?
                                .inceptionTime(inceptionTime)
                                .expirationTime(expirationTime)
                                .reason(reason)
                                .build();
                        PunishmentEntry entry = PunishmentEntry.of(punishment, type);
                        entries.add(entry);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return entries;
        });
    }

    @Override
    public CompletableFuture<List<PunishmentEntry>> queryPunisher(UUID punisher) {
        return CompletableFuture.supplyAsync(() -> {
            List<PunishmentEntry> entries = new ArrayList();

            try (PreparedStatement query = getConnection().prepareStatement(
                    "SELECT * FROM " + Table.PUNISHMENTS + " WHERE punisher = ?;")) {
                query.setString(1, punisher.toString());

                try (ResultSet rs = query.executeQuery()) {
                    while (rs.next()) {
                        int punishmentId = rs.getInt("punishment_id");
                        String serverContext = rs.getString("server_context");
                        PunishmentType type = PunishmentType.valueOf(rs.getString("punishment_type"));
                        UUIDOffender uuidOffender = new UUIDOffender(UUID.fromString(rs.getString("offender")));
                        String offenderUsername = rs.getString("offender_username");
                        long inceptionTime = rs.getLong("inception_time");
                        long expirationTime = rs.getLong("expiration_time");
                        String reason = rs.getString("reason");
                        Punishment punishment = Punishment.builder(punishmentId)
                                .serverContext(serverContext)
                                .punisher(punisher) // TODO: should set punisherUsername also?
                                .offender(uuidOffender)
                                .offenderUsername(offenderUsername)
                                .inceptionTime(inceptionTime)
                                .expirationTime(expirationTime)
                                .reason(reason)
                                .build();
                        PunishmentEntry entry = PunishmentEntry.of(punishment, type);
                        entries.add(entry);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return entries;
        });
    }

    @Override
    public CompletableFuture<Set<String>> queryAddresses(UUID user) {
        return CompletableFuture.supplyAsync(() -> {
            Set<String> addresses = new HashSet<>();

            try (PreparedStatement select = getConnection().prepareStatement(
                    "SELECT address FROM " + Table.ADDRESSES + " WHERE user = ?;")) {
                select.setString(1, user.toString());

                try (ResultSet rs = select.executeQuery()) {
                    while (rs.next()) {
                        addresses.add(rs.getString(1));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return addresses;
        });
    }

    @Override
    // TODO: fix insert or update
    public CompletableFuture<Void> insertAddress(UUID user, String address) {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement insert = getConnection().prepareStatement(
                    "INSERT INTO " + Table.ADDRESSES + " (user, address) VALUES (?, ?);")) {
                insert.setString(1, user.toString());
                insert.setString(2, address);
                insert.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        });
    }

    @Override
    public CompletableFuture<Set<UUID>> queryUsersOnAddress(String address) {
        return CompletableFuture.supplyAsync(() -> {
            Set<UUID> users = new HashSet<>();

            try (PreparedStatement select = getConnection().prepareStatement(
                    "SELECT user FROM " + Table.ADDRESSES + " WHERE address = ?;")) {
                select.setString(1, address);

                try (ResultSet rs = select.executeQuery()) {
                    while (rs.next()) {
                        users.add(UUID.fromString(rs.getString(1)));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return users;
        });
    }

    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
