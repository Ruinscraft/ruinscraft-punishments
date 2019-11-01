package com.ruinscraft.punishments;

import com.ruinscraft.punishments.behaviors.BanBehavior;
import com.ruinscraft.punishments.behaviors.PunishmentBehaviorRegistry;
import com.ruinscraft.punishments.offender.OnlineIPOffender;
import com.ruinscraft.punishments.offender.OnlineUUIDOffender;
import com.ruinscraft.punishments.offender.UUIDOffender;
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

import java.util.Optional;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPreJoin(AsyncPlayerPreLoginEvent event) {
        String address = event.getAddress().getHostAddress();
        PunishmentProfile uuidProfile = PunishmentProfiles.getOrLoadProfile(event.getUniqueId(), OnlineUUIDOffender.class).join();
        PunishmentProfile ipProfile = PunishmentProfiles.getOrLoadProfile(address, OnlineIPOffender.class).join();

        if (uuidProfile.offender instanceof UUIDOffender) {
            UUIDOffender uuidOffender = (UUIDOffender) uuidProfile.offender;

            uuidOffender.loadAddresses().join();
            uuidOffender.logAddress(address).join();
        }

        Punishment ban = null;

        if (uuidProfile.isBanned()) {
            ban = uuidProfile.getActive(PunishmentType.BAN);
        } else if (ipProfile.isBanned()) {
            ban = ipProfile.getActive(PunishmentType.BAN);
        }

        if (ban != null) {
            BanBehavior banBehavior = (BanBehavior) PunishmentBehaviorRegistry.get(PunishmentType.BAN);
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, banBehavior.getKickMessage(ban));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Optional<PunishmentProfile> uuidProfile = PunishmentProfiles.getProfile(player.getUniqueId());

        if (uuidProfile.get().hasExcessiveAmount()) {
            String message = Messages.COLOR_WARN + "You have an excessive amount of punishments. You are at risk of receiving amplified punishments. Check your punishments with /pinfo";
            Tasks.syncLater(() -> player.sendMessage(message), 3 * 20L);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PunishmentProfiles.unload(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PunishmentProfile uuidProfile = PunishmentProfiles.getProfile(player.getUniqueId()).get();
        PunishmentProfile ipProfile = PunishmentProfiles.getProfile(player.getAddress().getHostString()).get();
        Punishment mute = null;

        if (uuidProfile.isMuted()) {
            mute = uuidProfile.getActive(PunishmentType.MUTE);
        } else if (ipProfile.isMuted()) {
            mute = ipProfile.getActive(PunishmentType.MUTE);
        }

        if (mute != null) {
            player.sendMessage(Messages.COLOR_WARN + "You are muted for " + mute.getReason() + ". Expires in: " + mute.getRemainingDurationWords());
            event.setCancelled(true);
        }
    }

}
