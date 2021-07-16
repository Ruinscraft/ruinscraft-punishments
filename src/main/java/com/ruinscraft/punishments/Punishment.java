package com.ruinscraft.punishments;

import com.ruinscraft.punishments.offender.Offender;
import com.ruinscraft.punishments.util.Duration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Punishment {

    private int punishmentId;
    private String server;
    private UUID punisher;
    private String punisherUsername;
    private Offender offender;
    private String offenderUsername;
    private long inceptionTime;
    private long expirationTime;
    private String reason;

    // used with builder
    private Punishment() {

    }

    public static PunishmentBuilder builder() {
        return new PunishmentBuilder();
    }

    public static PunishmentBuilder builder(int punishmentId) {
        return new PunishmentBuilder(punishmentId);
    }

    public int getPunishmentId() {
        return punishmentId;
    }

    @Deprecated // only for internal use
    public void setPunishmentId(int punishmentId) {
        this.punishmentId = punishmentId;
    }

    public String getServer() {
        return server;
    }

    public boolean isThisServer() {
        String context = PunishmentsPlugin.get().getServerName();
        return getServer().equals(context);
    }

    public UUID getPunisher() {
        return punisher;
    }

    public String getPunisherUsername() {
        if (punisherUsername == null) {
            return "?";
        } else {
            return punisherUsername;
        }
    }

    public Offender getOffender() {
        return offender;
    }

    public void setOffender(Offender offender) {
        this.offender = offender;
    }

    public String getOffenderUsername() {
        if (offenderUsername == null) {
            return "?";
        } else {
            return offenderUsername;
        }
    }

    public long getInceptionTime() {
        return inceptionTime;
    }

    public String getInceptionTimeFormatted() {
        DateFormat df = new SimpleDateFormat("MM/dd/yy");
        return df.format(new Date(inceptionTime));
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public void setExpired() {
        this.expirationTime = System.currentTimeMillis();
    }

    public boolean isTemporary() {
        return expirationTime != -1L;
    }

    public long getTimeLeftMillis() {
        if (expirationTime == -1L) {
            return 0L;
        } else if (expirationTime < System.currentTimeMillis()) {
            return 0L;
        } else {
            return expirationTime - System.currentTimeMillis();
        }
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRemainingDurationWords() {
        return Duration.getRemainingDurationWords(this);
    }

    public String getTotalDurationWords() {
        return Duration.getTotalDurationWords(this);
    }

    public PunishmentEntry entry(PunishmentType type) {
        return PunishmentEntry.of(this, type);
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

        public PunishmentBuilder server(String server) {
            build.server = server;
            return this;
        }

        public PunishmentBuilder punisher(UUID punisher) {
            build.punisher = punisher;
            return this;
        }

        public PunishmentBuilder offender(Offender offender) {
            build.offender = offender;
            return this;
        }

        public PunishmentBuilder offenderUsername(String offenderUsername) {
            build.offenderUsername = offenderUsername;
            return this;
        }

        public PunishmentBuilder punisherUsername(String punisherUsername) {
            build.punisherUsername = punisherUsername;
            return this;
        }

        public PunishmentBuilder inceptionTime(long inceptionTime) {
            build.inceptionTime = inceptionTime;
            return this;
        }

        public PunishmentBuilder expirationTime(long expirationTime) {
            build.expirationTime = expirationTime;
            return this;
        }

        public PunishmentBuilder duration(long duration) {
            build.inceptionTime = System.currentTimeMillis();

            if (duration == -1L) {
                build.expirationTime = -1L;
            } else {
                build.expirationTime = build.inceptionTime + duration;
            }

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
