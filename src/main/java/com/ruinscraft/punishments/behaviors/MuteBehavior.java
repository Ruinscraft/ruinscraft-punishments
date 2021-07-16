package com.ruinscraft.punishments.behaviors;

import com.ruinscraft.punishments.PunishmentEntry;
import com.ruinscraft.punishments.util.Messages;

public class MuteBehavior extends PunishmentBehavior {

    @Override
    public void onCreate(PunishmentEntry entry) {
        notifyServer(entry);

        if (entry.punishment.getOffender().isOnline()) {
            entry.punishment.getOffender().sendMessage(Messages.COLOR_WARN + "You have been muted. Reason: " + entry.punishment.getReason());
        }
    }

    @Override
    public void onDelete(PunishmentEntry entry) {
        if (entry.punishment.getOffender().isOnline()) {
            entry.punishment.getOffender().sendMessage(Messages.COLOR_WARN + "A previous mute of yours has been deleted.");
        }
    }

    @Override
    public void onPardon(PunishmentEntry entry) {
        if (entry.punishment.getOffender().isOnline()) {
            entry.punishment.getOffender().sendMessage(Messages.COLOR_WARN + "Your current mute has been pardoned.");
        }
    }

}
