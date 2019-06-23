package com.ruinscraft.punishments;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class Punishment {

    private int punishmentId;
    private UUID punisher;
    private String offender; // UUID, IP, etc
    private long duration;
    private String reason;

    private Punishment() {
    } // used with builder

    public int getPunishmentId() {
        return punishmentId;
    }

    public UUID getPunisher() {
        return punisher;
    }

    public String getOffender() {
        return offender;
    }

    public long getDuration() {
        return duration;
    }

    public String getReason() {
        return reason;
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
            build.duration = duration;
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
