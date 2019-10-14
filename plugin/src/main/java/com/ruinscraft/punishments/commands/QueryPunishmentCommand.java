package com.ruinscraft.punishments.commands;

import com.ruinscraft.punishments.PlayerLookups;
import com.ruinscraft.punishments.PunishmentProfiles;
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

        if (args.length == 0 && sender instanceof Player) {
            target = sender.getName();
        } else {
            target = args[0];
        }

        sender.sendMessage(Messages.COLOR_MAIN + "Looking up punishment profile for " + target + "...");

        PlayerLookups.getUniqueId(target).thenAcceptAsync((uuid -> {
            PlayerLookups.getName(uuid).thenAcceptAsync((name) -> {
                if (uuid == null) {
                    sender.sendMessage(Messages.COLOR_WARN + name + " is not a valid Minecraft username.");
                    return;
                }

                PunishmentProfiles.getOrLoadProfile(uuid, UUIDOffender.class).thenAcceptAsync((profile -> {
                    profile.show(sender);
                }));
            });
        }));

        return true;
    }

}
