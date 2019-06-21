package com.ruinscraft.punishments;

import java.util.UUID;

public class Punishment {

    private int punishmentId;
    private UUID punisher;
    private String offender; // UUID, IP, etc
    private long duration;
    private String reason;

    public Punishment(UUID punisher, String offender, long duration, String reason) {
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
    
}
