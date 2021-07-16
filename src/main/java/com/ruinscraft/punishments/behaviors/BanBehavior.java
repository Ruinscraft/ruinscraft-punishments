package com.ruinscraft.punishments.behaviors;

import com.ruinscraft.punishments.Punishment;
import com.ruinscraft.punishments.PunishmentEntry;
import com.ruinscraft.punishments.util.Messages;

import java.util.StringJoiner;

public class BanBehavior extends KickablePunishmentBehavior {

    @Override
    public String getKickMessage(Punishment punishment) {
        StringJoiner kickMsg = new StringJoiner("\n");
        kickMsg.add(Messages.COLOR_WARN + "You have been banned.");
        kickMsg.add("");
        kickMsg.add(Messages.COLOR_MAIN + "Reason: " + punishment.getReason());
        kickMsg.add(Messages.COLOR_MAIN + "Server: " + punishment.getServer());
        kickMsg.add("Expires in: " + punishment.getRemainingDurationWords());
        kickMsg.add("");
        kickMsg.add("Appeal @ " + APPEAL_LINK);
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
            entry.punishment.getOffender().sendMessage(Messages.COLOR_WARN + "A previous ban of yours has been deleted.");
        }
    }

    @Override
    public void onPardon(PunishmentEntry entry) {
        if (entry.punishment.getOffender().isOnline()) {
            entry.punishment.getOffender().sendMessage(Messages.COLOR_WARN + "Your current ban has been pardoned.");
        }
    }

}
