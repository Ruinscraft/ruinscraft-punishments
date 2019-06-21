package com.ruinscraft.punishments;

import com.ruinscraft.punishments.dispatcher.PunishmentDispatcher;

import java.util.UUID;

public class Punishment {

    private int punishmentId;
    private UUID punisher;
    private String offender; // UUID, IP, etc
    private long duration;
    private String reason;

    private Punishment(UUID punisher, String offender, long duration, String reason) {
        this.punisher = punisher;
        this.offender = offender;
        this.duration = duration;
        this.reason = reason;
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

    public long getDuration() {
        return duration;
    }

    public String getReason() {
        return reason;
    }

    public void dispatch(PunishmentType type) {
        PunishmentDispatcher.dispatch(PunishmentEntry.of(this, type));
    }

    public static PunishmentBuilder builder() {
        return new PunishmentBuilder();
    }

    public static class PunishmentBuilder {
        private Punishment canidate;

        private PunishmentBuilder() {}

        public PunishmentBuilder punisher(UUID punisher) {
            canidate.punisher = punisher;
            return this;
        }

        public PunishmentBuilder offender(String offender) {
            canidate.offender = offender;
            return this;
        }

        public PunishmentBuilder duration(long duration) {
            canidate.duration = duration;
            return this;
        }

        public PunishmentBuilder reason(String reason) {
            canidate.reason = reason;
            return this;
        }

        public Punishment build() {
            return canidate;
        }
    }

}
