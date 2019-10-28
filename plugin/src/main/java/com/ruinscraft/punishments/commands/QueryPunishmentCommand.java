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
            target = sender.getName().trim();
        } else if (ip && args.length == 0 && sender instanceof Player) {
            Player player = (Player) sender;
            target = player.getAddress().getHostString().trim();
        } else {
            target = args[0].trim();
        }

        if ((target.contains(".") || target.contains(":")) && !ip) {
            sender.sendMessage(Messages.COLOR_WARN + "Not a valid username. Did you mean to use /" + label.toLowerCase() + "ip?");
            return true;
        }

        sender.sendMessage(Messages.COLOR_MAIN + "Looking up Punishment Profile for " + target + "...");

        CompletableFuture.runAsync(() -> {
            PunishmentProfile profile = null;

            try {
                if (ip) {
                    profile = PunishmentProfiles.getOrLoadProfile(target, IPOffender.class).get(); // TODO: #join() ?
                } else {
                    UUID targetUUID = PlayerLookups.getUniqueId(target).get();
                    profile = PunishmentProfiles.getOrLoadProfile(targetUUID, UUIDOffender.class).get(); // TODO: #join() ?
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (profile == null) {
                sender.sendMessage(Messages.COLOR_WARN + "Could not load Punishment Profile for " + target);
                return;
            }

            profile.show(sender);
        });

        return true;
    }

}
