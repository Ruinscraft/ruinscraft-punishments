package com.ruinscraft.punishments.behaviors;

import com.ruinscraft.punishments.Punishment;
import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentEntry;
import com.ruinscraft.punishments.PunishmentType;
import org.bukkit.Bukkit;

public interface PunishmentBehavior {

    void perform(Punishment punishment, PunishmentAction action);

    default void notifyServer(Punishment punishment, PunishmentType type, PunishmentAction action) {
        PunishmentEntry entry = PunishmentEntry.of(punishment, type);

        if (action == PunishmentAction.CREATE) {
            Bukkit.broadcastMessage(entry.creationMessage());
        }
    }

}
