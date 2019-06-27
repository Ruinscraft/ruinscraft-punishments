
import java.sql.*;
import java.util.UUID;
import java.util.regex.Pattern;

public class BanManagerImport {

    private static final String BM_DATABASE = "banmanager";
    private static final String BM_TABLE_WARNS = "bm_player_warnings";
    private static final String BM_TABLE_MUTES = "bm_player_mutes";
    private static final String BM_TABLE_BANS = "bm_player_bans";
    private static final String RC_PUNISHMENTS_DATABASE = "punishments";
    // (punishment_id*, punishment_type, punisher, offender, inception_time, expiration_time, reason)
    private static final String RC_PUNISHMENTS_TABLE = "ruinscraft_punishments";

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Missing arguments <username> and <password>");
            System.out.println("Example: java -jar BMImport.jar root password");
            return;
        }

        System.out.println("Starting BanManager import utility...");

        Connection bm_conn;
        Connection rc_conn;

        try {
            bm_conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + BM_DATABASE, args[0], args[1]);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        try {
            rc_conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + RC_PUNISHMENTS_DATABASE, args[0], args[1]);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        try {
            if (bm_conn.isClosed()) {
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        try {
            if (rc_conn.isClosed()) {
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        int insertedWarns = 0;
        int insertedMutes = 0;
        int insertedBans = 0;

        System.out.println("Importing warns...");

        // import warns
        try (PreparedStatement select = bm_conn.prepareStatement(
                "SELECT LOWER(HEX(player_id)) AS offender, LOWER(HEX(actor_id)) AS punisher, created, expires, reason FROM " + BM_TABLE_WARNS)) {
            try (ResultSet rs = select.executeQuery()) {
                while (rs.next()) {
                    UUID offender = formatFromInput(rs.getString("offender"));
                    UUID punisher = formatFromInput(rs.getString("punisher"));
                    long created = rs.getLong("created");
                    long expires = rs.getLong("expires");
                    if (expires == 0) {
                        expires = -1L;
                    }
                    String reason = rs.getString("reason");

                    try (PreparedStatement insert = createInsertStatement(rc_conn)) {
                        insert.setString(1, "WARN");
                        insert.setString(2, punisher.toString());
                        insert.setString(3, offender.toString());
                        insert.setLong(4, created);
                        insert.setLong(5, expires);
                        insert.setString(6, reason);
                        insert.execute();
                    }

                    insertedWarns++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Importing mutes...");

        // import mutes
        try (PreparedStatement select = bm_conn.prepareStatement(
                "SELECT LOWER(HEX(player_id)) AS offender, LOWER(HEX(actor_id)) AS punisher, created, expires, reason FROM " + BM_TABLE_MUTES)) {
            try (ResultSet rs = select.executeQuery()) {
                while (rs.next()) {
                    UUID offender = formatFromInput(rs.getString("offender"));
                    UUID punisher = formatFromInput(rs.getString("punisher"));
                    long created = rs.getLong("created");
                    long expires = rs.getLong("expires");
                    if (expires == 0) {
                        expires = -1L;
                    }
                    String reason = rs.getString("reason");

                    try (PreparedStatement insert = createInsertStatement(rc_conn)) {
                        insert.setString(1, "MUTE");
                        insert.setString(2, punisher.toString());
                        insert.setString(3, offender.toString());
                        insert.setLong(4, created);
                        insert.setLong(5, expires);
                        insert.setString(6, reason);
                        insert.execute();
                    }

                    insertedMutes++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Importing bans...");
        
        // import bans
        try (PreparedStatement select = bm_conn.prepareStatement(
                "SELECT LOWER(HEX(player_id)) AS offender, LOWER(HEX(actor_id)) AS punisher, created, expires, reason FROM " + BM_TABLE_BANS)) {
            try (ResultSet rs = select.executeQuery()) {
                while (rs.next()) {
                    UUID offender = formatFromInput(rs.getString("offender"));
                    UUID punisher = formatFromInput(rs.getString("punisher"));
                    long created = rs.getLong("created");
                    long expires = rs.getLong("expires");
                    if (expires == 0) {
                        expires = -1L;
                    }
                    String reason = rs.getString("reason");

                    try (PreparedStatement insert = createInsertStatement(rc_conn)) {
                        insert.setString(1, "BAN");
                        insert.setString(2, punisher.toString());
                        insert.setString(3, offender.toString());
                        insert.setLong(4, created);
                        insert.setLong(5, expires);
                        insert.setString(6, reason);
                        insert.execute();
                    }

                    insertedBans++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Inserted " + insertedWarns + " warns");
        System.out.println("Inserted " + insertedMutes + " mutes");
        System.out.println("Inserted " + insertedBans + " bans");
    }

    private static PreparedStatement createInsertStatement(Connection conn) throws SQLException {
        return conn.prepareStatement(
                "INSERT INTO "
                        + RC_PUNISHMENTS_TABLE
                        + " (punishment_type, punisher, offender, inception_time, expiration_time, reason) VALUES (?, ?, ?, ?, ?, ?);");
    }

    private static final Pattern UUID_FIX = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

    private static UUID formatFromInput(String uuidWithoutDashes) {
        return UUID.fromString(UUID_FIX.matcher(uuidWithoutDashes.replace("-", "")).replaceAll("$1-$2-$3-$4-$5"));
    }

}
