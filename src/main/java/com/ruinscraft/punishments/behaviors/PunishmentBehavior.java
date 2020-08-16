package com.ruinscraft.punishments.behaviors;

import com.ruinscraft.punishments.Punishment;
import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentEntry;
import com.ruinscraft.punishments.PunishmentType;
import com.ruinscraft.punishments.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public interface PunishmentBehavior {

    void perform(Punishment punishment, PunishmentAction action);

    default void notifyServer(Punishment punishment, PunishmentType type, PunishmentAction action) {
        PunishmentEntry entry = PunishmentEntry.of(punishment, type);

        String creationMessage = entry.creationMessage(false);
        String creationMessagePrivileged = entry.creationMessage(true);

        Tasks.sync(() -> {
            if (action == PunishmentAction.CREATE) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.hasPermission("ruinscraft.punishments.moreinfo")) {
                        player.sendMessage(creationMessagePrivileged);
                    } else {
                        player.sendMessage(creationMessage);
                    }
                }
            }
        });
    }

}
