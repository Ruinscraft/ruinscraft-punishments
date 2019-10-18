package com.ruinscraft.punishments.commands;

import com.ruinscraft.punishments.PlayerLookups;
import com.ruinscraft.punishments.PunishmentProfiles;
import com.ruinscraft.punishments.offender.IPOffender;
import com.ruinscraft.punishments.offender.UUIDOffender;
import com.ruinscraft.punishments.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        if (ip) {
            PunishmentProfiles.getOrLoadProfile(target, IPOffender.class).thenAcceptAsync(profile -> {
                profile.show(sender);
            });
        } else {
            PlayerLookups.getUniqueId(target).thenAcceptAsync(uuid -> {
                if (uuid == null) {
                    sender.sendMessage(Messages.COLOR_WARN + "Mojang UUID for " + args[0] + " not found.");
                    return;
                }

                PunishmentProfiles.getOrLoadProfile(uuid, UUIDOffender.class).thenAcceptAsync(profile -> {
                    profile.show(sender);
                });
            });
        }

        return true;
    }

}
