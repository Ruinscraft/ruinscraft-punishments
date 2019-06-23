package com.ruinscraft.punishments;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPreJoin(AsyncPlayerPreLoginEvent event) {
        final String uuidString = event.getUniqueId().toString();

        try {
            PunishmentProfile profile = PunishmentProfile.load(uuidString).call();

            if (profile.isBanned()) {
                Punishment ban = profile.getActive(PunishmentType.BAN);
                event.disallow(
                        AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                        "You have been banned.\nReason: " + ban.getReason());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        PunishmentProfile.unload(event.getPlayer().getName());
    }

}
