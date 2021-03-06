package com.ruinscraft.punishments;

import com.ruinscraft.punishments.util.Messages;

import java.util.StringJoiner;

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

    public void performAction(PunishmentAction action) {
        action.performRemote(this);
    }

    public String creationMessage(boolean showPunisher) {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(Messages.COLOR_WARN + punishment.getOffenderUsername());
        joiner.add("has been");
        joiner.add(type.getVerb());
        if (showPunisher) {
            joiner.add("by");
            joiner.add(punishment.getPunisherUsername());
        }
        joiner.add("for");
        joiner.add(punishment.getReason() + ".");
        if (punishment.isTemporary()) {
            joiner.add("Expires in:");
            joiner.add(punishment.getTotalDurationWords());
        }
        return joiner.toString();
    }

}
