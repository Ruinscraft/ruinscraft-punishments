package com.ruinscraft.punishments.commands;

import com.ruinscraft.punishments.PlayerLookups;
import com.ruinscraft.punishments.PunishmentProfile;
import com.ruinscraft.punishments.PunishmentProfiles;
import com.ruinscraft.punishments.offender.IPOffender;
import com.ruinscraft.punishments.offender.UUIDOffender;
import com.ruinscraft.punishments.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class QueryPunishmentCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final String target;
        boolean ip = label.endsWith("ip");

        if (!ip && args.length == 0 && sender instanceof Player) {
            target = sender.getName();
        } else if (ip && args.length == 0 && sender instanceof Player) {
            Player player = (Player) sender;
            target = player.getAddress().getHostString();
        } else {
            target = args[0];
        }

        sender.sendMessage(Messages.COLOR_MAIN + "Looking up punishment profile for " + target + "...");

        CompletableFuture.runAsync(() -> {
            PunishmentProfile profile;

            if (ip) {
                profile = PunishmentProfiles.getOrLoadProfile(args[0], IPOffender.class).join();
            } else {
                UUID targetUUID = PlayerLookups.getUniqueId(args[0]).join();
                profile = PunishmentProfiles.getOrLoadProfile(targetUUID, UUIDOffender.class).join();
            }

            if (profile == null) {
                sender.sendMessage(Messages.COLOR_WARN + "Could not load Punishment Profile for " + args[0]);
                return;
            }

            profile.show(sender);
        });

        return true;
    }

}
