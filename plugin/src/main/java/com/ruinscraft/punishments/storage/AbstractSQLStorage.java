package com.ruinscraft.punishments.storage;

import com.ruinscraft.punishments.Punishment;
import com.ruinscraft.punishments.PunishmentEntry;
import com.ruinscraft.punishments.PunishmentType;
import com.ruinscraft.punishments.offender.Offender;
import com.ruinscraft.punishments.offender.UUIDOffender;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractSQLStorage implements Storage {

    public void createTables() throws SQLException {
        String[] stmts = new String[]{
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
                        "PRIMARY KEY (punishment_id));",
                "CREATE TABLE IF NOT EXISTS " +
                        Table.ADDRESSES +
                        " (user VARCHAR(36) NOT NULL, " +
                        "address VARCHAR(127) NOT NULL);"
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
            String stmt = "INSERT INTO " + Table.PUNISHMENTS + "(server_context, punishment_type, punisher, punisher_username, offender, offender_username, inception_time, expiration_time, reason) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

            try (Connection connection = getConnection();
                 PreparedStatement insert = connection.prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS)) {
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
            String stmt = "SELECT * FROM " + Table.PUNISHMENTS + " WHERE punisher = ?;";

            try (Connection connection = getConnection();
                 PreparedStatement query = connection.prepareStatement(stmt)) {
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
            String stmt = "SELECT address FROM " + Table.ADDRESSES + " WHERE user = ?;";

            try (Connection connection = getConnection();
                 PreparedStatement select = connection.prepareStatement(stmt)) {
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
            String stmt = "INSERT INTO " + Table.ADDRESSES + " (user, address) VALUES (?, ?);";

            try (Connection connection = getConnection();
                 PreparedStatement insert = connection.prepareStatement(stmt)) {
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
            String stmt = "SELECT user FROM " + Table.ADDRESSES + " WHERE address = ?;";

            try (Connection connection = getConnection();
                 PreparedStatement select = connection.prepareStatement(stmt)) {
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

    public abstract Connection getConnection();

    protected final class Table {
        protected static final String PUNISHMENTS = "ruinscraft_punishments";
        protected static final String ADDRESSES = "ruinscraft_addresses";
    }

}
