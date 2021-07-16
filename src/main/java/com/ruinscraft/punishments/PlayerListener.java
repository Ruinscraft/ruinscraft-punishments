package com.ruinscraft.punishments;

import com.ruinscraft.punishments.behaviors.BanBehavior;
import com.ruinscraft.punishments.behaviors.PunishmentBehaviorRegistry;
import com.ruinscraft.punishments.offender.IPOffender;
import com.ruinscraft.punishments.offender.UUIDOffender;
import com.ruinscraft.punishments.util.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    /*
     *  We use #join on CompletableFuture here to block the login until all Punishment information
     *  about a user has been loaded. This event is async, so this is safe.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPreJoin(AsyncPlayerPreLoginEvent event) {
        String address = event.getAddress().getHostAddress();

        UUIDOffender uuidOffender = new UUIDOffender(event.getUniqueId());
        IPOffender ipOffender = new IPOffender(address);

        // Block until profiles are loaded by using #join
        PunishmentProfile uuidProfile = PunishmentProfiles.getOrLoadProfile(uuidOffender).join();
        PunishmentProfile ipProfile = PunishmentProfiles.getOrLoadProfile(ipOffender).join();

        // Log the address being used
        {
            AddressLog addressLog = AddressLog.of(event);
            uuidOffender.saveAddressLog(addressLog);
        }

        final Punishment ban;

        if (uuidProfile.isBanned()) {
            ban = uuidProfile.getActive(PunishmentType.BAN);
        } else if (ipProfile.isBanned()) {
            ban = ipProfile.getActive(PunishmentType.BAN);
        } else {
            ban = null;
        }

        if (ban != null) {
            BanBehavior banBehavior = (BanBehavior) PunishmentBehaviorRegistry.get(PunishmentType.BAN);
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, banBehavior.getKickMessage(ban));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        // TODO:
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        UUIDOffender uuidOffender = new UUIDOffender(player.getUniqueId());
        IPOffender ipOffender = new IPOffender(player.getAddress().getHostString());

        PunishmentProfile uuidProfile = PunishmentProfiles.getOrLoadProfile(uuidOffender).join();
        PunishmentProfile ipProfile = PunishmentProfiles.getOrLoadProfile(ipOffender).join();

        final Punishment mute;

        if (uuidProfile.isMuted()) {
            mute = uuidProfile.getActive(PunishmentType.MUTE);
        } else if (ipProfile.isMuted()) {
            mute = ipProfile.getActive(PunishmentType.MUTE);
        } else {
            mute = null;
        }

        if (mute != null) {
            player.sendMessage(Messages.COLOR_WARN + "You are muted for " + mute.getReason() + ". Expires in: " + mute.getRemainingDurationWords());
            event.setCancelled(true);
        }
    }

}
