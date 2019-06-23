package com.ruinscraft.punishments;

import com.ruinscraft.punishments.util.Duration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class Punishment {

    private int punishmentId;
    private UUID punisher;
    private String offender; // UUID, IP, etc
    private long inceptionTime;
    private long expirationTime;
    private String reason;

    private Punishment() {
    } // used with builder

    @Deprecated // only for internal use
    public void setPunishmentId(int punishmentId) {
        this.punishmentId = punishmentId;
    }

    public int getPunishmentId() {
        return punishmentId;
    }

    public UUID getPunisher() {
        return punisher;
    }

    public String getOffender() {
        return offender;
    }

    public long getInceptionTime() {
        return inceptionTime;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public String getReason() {
        return reason;
    }

    public String getRemainingDurationWords() {
        return Duration.getRemainingDurationWords(this);
    }

    public Optional<Player> getOffenderPlayer() {
        final UUID offenderUUID;
        try {
            offenderUUID = UUID.fromString(offender);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
        return Optional.of(Bukkit.getPlayer(offenderUUID));
    }

    public Optional<String> getOffenderUsername() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(offender);

        if (offlinePlayer.hasPlayedBefore()) {
            return Optional.of(offlinePlayer.getName());
        } else {
            return Optional.empty();
        }
    }

    public PunishmentEntry entry(PunishmentType type) {
        return PunishmentEntry.of(this, type);
    }

    public static PunishmentBuilder builder() {
        return new PunishmentBuilder();
    }

    public static PunishmentBuilder builder(int punishmentId) {
        return new PunishmentBuilder(punishmentId);
    }

    public static class PunishmentBuilder {
        private Punishment build;

        private PunishmentBuilder() {
            build = new Punishment();
        }

        private PunishmentBuilder(int punishmentId) {
            build = new Punishment();
            build.punishmentId = punishmentId;
        }

        public PunishmentBuilder punisher(UUID punisher) {
            build.punisher = punisher;
            return this;
        }

        public PunishmentBuilder offender(String offender) {
            build.offender = offender;
            return this;
        }

        public PunishmentBuilder duration(long duration) {
            build.inceptionTime = System.currentTimeMillis();
            build.expirationTime = build.inceptionTime + duration;
            return this;
        }

        public PunishmentBuilder reason(String reason) {
            build.reason = reason;
            return this;
        }

        public Punishment build() {
            return build;
        }
    }

}
