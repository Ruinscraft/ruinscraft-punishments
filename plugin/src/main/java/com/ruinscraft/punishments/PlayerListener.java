package com.ruinscraft.punishments;

import com.ruinscraft.punishments.behaviors.BanBehavior;
import com.ruinscraft.punishments.behaviors.PunishmentBehaviorRegistry;
import com.ruinscraft.punishments.util.Messages;
import com.ruinscraft.punishments.util.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPreJoin(AsyncPlayerPreLoginEvent event) {
        PunishmentProfile profile;

        try {
            profile = PunishmentProfile.load(event.getUniqueId()).call();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (profile.isBanned()) {
            Punishment ban = profile.getActive(PunishmentType.BAN);
            BanBehavior banBehavior = (BanBehavior) PunishmentBehaviorRegistry.get(PunishmentType.BAN);
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, banBehavior.getKickMessage(ban));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PunishmentProfile profile = PunishmentProfile.get(player.getUniqueId());

        if (profile.hasExcessiveAmount()) {
            Tasks.syncLater(() -> player.sendMessage(Messages.COLOR_WARN + "You have an excessive amount of punishments. You are at risk of receiving amplified punishments."),
                    3 * 20L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        PunishmentProfile.unload(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PunishmentProfile profile = PunishmentProfile.get(player.getUniqueId());

        if (profile.isMuted()) {
            Punishment mute = profile.getActive(PunishmentType.MUTE);
            player.sendMessage(Messages.COLOR_WARN + "You are muted for " + mute.getReason() + ". Expires in: " + mute.getRemainingDurationWords());
            event.setCancelled(true);
        }
    }

}
