package com.ruinscraft.punishments.commands;

import com.ruinscraft.punishments.PlayerLookups;
import com.ruinscraft.punishments.PunishmentProfile;
import com.ruinscraft.punishments.util.Messages;
import com.ruinscraft.punishments.util.Tasks;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class QueryPunishmentCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("/" + label + " <username>");
            return true;
        }

        sender.sendMessage(Messages.COLOR_MAIN + "Looking up punishment profile for " + args[0] + "...");

        Tasks.async(() -> {
            final UUID target;

            try {
                target = PlayerLookups.getUniqueId(args[0]).call();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            if (target == null) {
                sender.sendMessage(Messages.COLOR_WARN + args[0] + " is not a valid Minecraft username.");
                return;
            }

            final PunishmentProfile profile;

            try {
                profile = PunishmentProfile.getOrLoad(target).call();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            if (profile == null) {
                sender.sendMessage(Messages.COLOR_WARN + "Profile for " + args[0] + " could not be loaded.");
                return;
            }

            profile.show(sender);
        });

        return true;
    }

}
