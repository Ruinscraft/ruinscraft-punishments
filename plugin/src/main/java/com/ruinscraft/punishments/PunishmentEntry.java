package com.ruinscraft.punishments;

public class PunishmentEntry {

    public final Punishment punishment;
    public final PunishmentType type;

    private PunishmentEntry(Punishment punishment, PunishmentType type) {
        this.punishment = punishment;
        this.type = type;
    }

    public static PunishmentEntry of(Punishment punishment, PunishmentType type) {
        return new PunishmentEntry(punishment, type);
    }

    public void action(PunishmentAction action) {
        action.sync();
    }

}
