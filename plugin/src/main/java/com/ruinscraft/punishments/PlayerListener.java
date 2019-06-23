package com.ruinscraft.punishments;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPreJoin(AsyncPlayerPreLoginEvent event) {
        final String username = event.getName();

        try {
            PunishmentProfile profile = PunishmentProfile.load(username).call();

            if (profile.isBanned()) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, "you are banned.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
