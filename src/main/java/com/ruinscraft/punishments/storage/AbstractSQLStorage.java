package com.ruinscraft.punishments.storage;

import com.ruinscraft.punishments.AddressLog;
import com.ruinscraft.punishments.Punishment;
import com.ruinscraft.punishments.PunishmentEntry;
import com.ruinscraft.punishments.PunishmentType;
import com.ruinscraft.punishments.offender.Offender;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractSQLStorage implements Storage {

    public void createTables() throws SQLException {
        String[] stmts = new String[]{
                "CREATE TABLE IF NOT EXISTS " +
                        Table.PUNISHMENTS +
                        " (punishment_id INT NOT NULL AUTO_INCREMENT, " +
                        "server VARCHAR(64), " +
                        "punishment_type VARCHAR(12), " +
                        "punisher VARCHAR(36), " +
                        "punisher_username VARCHAR(16), " + // Added 2.0-SNAPSHOT
                        "offender VARCHAR(36), " +
                        "offender_username VARCHAR(16), " + // Added 2.0-SNAPSHOT
                        "inception_time BIGINT, " +
                        "expiration_time BIGINT, " +
                        "reason VARCHAR(255)," +
                        "PRIMARY KEY (punishment_id));",
                "CREATE TABLE IF NOT EXISTS " +
                        Table.ADDRESSES +
                        " (user VARCHAR(36) NOT NULL, " +
                        "address VARCHAR(128) NOT NULL, " +
                        "username VARCHAR(16) NOT NULL, " +
                        "used_at BIGINT, " +
                        "UNIQUE (user, address));",
                // Alter for 2.0-SNAPSHOT changes
                "ALTER TABLE " +
                        Table.PUNISHMENTS +
                        " ADD COLUMN IF NOT EXISTS punisher_username VARCHAR(16) AFTER punisher, " +
                        "ADD COLUMN IF NOT EXISTS offender_username VARCHAR(16) AFTER offender;",
                "ALTER TABLE " +
                        Table.PUNISHMENTS +
                        " CHANGE COLUMN IF EXISTS server_context server VARCHAR(64) AFTER punishment_id;"
        };

        try (Connection connection = getConnection()) {
            for (String stmt : stmts) {
                try (PreparedStatement create_table = connection.prepareStatement(stmt)) {
                    create_table.execute();
                }
            }
        }
    }

    @Override
    public CompletableFuture<Void> insert(PunishmentEntry entry) {
        return CompletableFuture.supplyAsync(() -> {
            String stmt = "INSERT INTO " + Table.PUNISHMENTS + "(server, punishment_type, punisher, punisher_username, offender, offender_username, inception_time, expiration_time, reason) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

            try (Connection connection = getConnection();
                 PreparedStatement insert = connection.prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS)) {
                insert.setString(1, entry.punishment.getServer());
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

            String stmt = "UPDATE " + Table.PUNISHMENTS + " SET expiration_time = ?, reason = ? WHERE punishment_id = ?;";

            try (Connection connection = getConnection();
                 PreparedStatement update = connection.prepareStatement(stmt)) {
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
            String stmt = "DELETE FROM " + Table.PUNISHMENTS + " WHERE punishment_id = ?;";

            try (Connection connection = getConnection();
                 PreparedStatement delete = connection.prepareStatement(stmt)) {
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
            String stmt = "SELECT * FROM " + Table.PUNISHMENTS + " WHERE offender = ?;";

            try (Connection connection = getConnection();
                 PreparedStatement query = connection.prepareStatement(stmt)) {
                query.setString(1, offender.toString());

                try (ResultSet rs = query.executeQuery()) {
                    while (rs.next()) {
                        int punishmentId = rs.getInt("punishment_id");
                        String server = rs.getString("server");
                        PunishmentType type = PunishmentType.valueOf(rs.getString("punishment_type"));
                        UUID punisher = UUID.fromString(rs.getString("punisher"));
                        String punisherUsername = rs.getString("punisher_username");
                        String offenderUsername = rs.getString("offender_username");
                        long inceptionTime = rs.getLong("inception_time");
                        long expirationTime = rs.getLong("expiration_time");
                        String reason = rs.getString("reason");
                        Punishment punishment = Punishment.builder(punishmentId)
                                .server(server)
                                .punisher(punisher)
                                .punisherUsername(punisherUsername)
                                .offender(offender)
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
    public CompletableFuture<List<AddressLog>> queryAddressLogs(UUID user) {
        return CompletableFuture.supplyAsync(() -> {
            List<AddressLog> addressLogs = new ArrayList<>();

            try (Connection connection = getConnection();
                 PreparedStatement query = connection.prepareStatement("SELECT * FROM " + Table.ADDRESSES + " WHERE user = ?;")) {
                query.setString(1, user.toString());

                try (ResultSet rs = query.executeQuery()) {
                    while (rs.next()) {
                        String address = rs.getString("address");
                        String username = rs.getString("username");
                        long usedAt = rs.getLong("used_at");
                        AddressLog addressLog = new AddressLog(user, address, username, usedAt);

                        addressLogs.add(addressLog);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return addressLogs;
        });
    }

    @Override
    public CompletableFuture<List<AddressLog>> queryAddressLogs(String address) {
        return CompletableFuture.supplyAsync(() -> {
            List<AddressLog> addressLogs = new ArrayList<>();

            try (Connection connection = getConnection();
                 PreparedStatement query = connection.prepareStatement("SELECT * FROM " + Table.ADDRESSES + " WHERE address = ?;")) {
                query.setString(1, address);

                try (ResultSet rs = query.executeQuery()) {
                    while (rs.next()) {
                        UUID user = UUID.fromString(rs.getString("user"));
                        String username = rs.getString("username");
                        long usedAt = rs.getLong("used_at");
                        AddressLog addressLog = new AddressLog(user, address, username, usedAt);

                        addressLogs.add(addressLog);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return addressLogs;
        });
    }

    @Override
    public CompletableFuture<Void> insertAddressLog(AddressLog addressLog) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement insert = connection.prepareStatement("INSERT INTO " + Table.ADDRESSES + " (user, address, username, used_at) VALUES (?, ?, ?, ?);")) {
                insert.setString(1, addressLog.getUser().toString());
                insert.setString(2, addressLog.getAddress());
                insert.setString(3, addressLog.getUsername());
                insert.setLong(4, addressLog.getUsedAt());
                insert.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public abstract Connection getConnection();

    protected final class Table {
        protected static final String PUNISHMENTS = "punishments";
        protected static final String ADDRESSES = "addresses";
    }

}
