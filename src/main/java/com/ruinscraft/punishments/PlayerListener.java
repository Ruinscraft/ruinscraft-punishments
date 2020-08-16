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

public class PlayerListener implements Listener {

    /*
     *  We use #join on CompletableFuture here to block the login until all Punishment information
     *  about a user has been loaded. This event is async, so this is safe.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPreJoin(AsyncPlayerPreLoginEvent event) {
        if (!event.isAsynchronous()) {
            PunishmentsPlugin.get().getLogger().warning("AsyncPlayerPreLoginEvent was not async! Player: " + event.getName());
        }

        String address = event.getAddress().getHostAddress();

        // Block until profiles are loaded by using #join
        PunishmentProfile uuidProfile = PunishmentProfiles.getOrLoadProfile(event.getUniqueId(), OnlineUUIDOffender.class).join();
        PunishmentProfile ipProfile = PunishmentProfiles.getOrLoadProfile(address, OnlineIPOffender.class).join();

        // Log the address being used
        {
            UUIDOffender uuidOffender = (UUIDOffender) uuidProfile.offender;
            AddressLog addressLog = AddressLog.of(event);

            uuidOffender.saveAddressLog(addressLog).join();
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
        PunishmentProfile profile = PunishmentProfiles.getProfile(player.getUniqueId()).get();

        if (profile.hasExcessiveAmount()) {
            String message = Messages.COLOR_WARN + "You have an excessive amount of punishments. You are at risk of receiving amplified punishments. Check your punishments with /pinfo";
            Tasks.syncLater(() -> player.sendMessage(message), 3 * 20L);
        }

        // TODO: alert of evades
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
