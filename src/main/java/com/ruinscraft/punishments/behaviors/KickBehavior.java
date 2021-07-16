package com.ruinscraft.punishments.behaviors;

import com.ruinscraft.punishments.Punishment;
import com.ruinscraft.punishments.PunishmentEntry;
import com.ruinscraft.punishments.util.Messages;

import java.util.StringJoiner;

public class KickBehavior extends KickablePunishmentBehavior {

    @Override
    public String getKickMessage(Punishment punishment) {
        StringJoiner kickMsg = new StringJoiner("\n");
        kickMsg.add(Messages.COLOR_WARN + "You have been kicked.");
        kickMsg.add("");
        kickMsg.add(Messages.COLOR_MAIN + "Reason: " + punishment.getReason());
        return kickMsg.toString();
    }

    @Override
    public void onCreate(PunishmentEntry entry) {
        notifyServer(entry);

        if (entry.punishment.getOffender().isOnline()) {
            entry.punishment.getOffender().kick(getKickMessage(entry.punishment));
        }
    }

    @Override
    public void onDelete(PunishmentEntry entry) {
        if (entry.punishment.getOffender().isOnline()) {
            entry.punishment.getOffender().sendMessage(Messages.COLOR_WARN + "A previous kick of yours has been deleted.");
        }
    }

    @Override
    public void onPardon(PunishmentEntry entry) {

    }

}
