package com.ruinscraft.punishments.commands;

import com.ruinscraft.punishments.PlayerLookups;
import com.ruinscraft.punishments.PunishmentProfile;
import com.ruinscraft.punishments.PunishmentsPlugin;
import com.ruinscraft.punishments.offender.UUIDOffender;
import com.ruinscraft.punishments.util.Messages;
import com.ruinscraft.punishments.util.Tasks;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class QueryPunishmentCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 && sender instanceof Player) {
            args = new String[]{sender.getName()};
        }

        sender.sendMessage(Messages.COLOR_MAIN + "Looking up punishment profile for " + args[0] + "...");

        return lookup(sender, args);
    }

    private boolean lookup(CommandSender caller, String[] args) {
        Tasks.async(() -> {
            final UUID target;

            try {
                target = PlayerLookups.getUniqueId(args[0]).call();
                args[0] = PlayerLookups.getName(target).call();
            } catch (Exception e) {
                return;
            }

            if (target == null) {
                caller.sendMessage(Messages.COLOR_WARN + args[0] + " is not a valid Minecraft username.");
                return;
            }

            UUIDOffender uuidOffender = new UUIDOffender(target);

            final PunishmentProfile profile;

            try {
                profile = PunishmentProfile.getOrLoad(uuidOffender).call();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            if (profile == null) {
                caller.sendMessage(Messages.COLOR_WARN + "Profile for " + args[0] + " could not be loaded.");
                return;
            }

            try {
                profile.show(caller).call();
            } catch (Exception e) {
                e.printStackTrace();
            }

            List<String> alts = new ArrayList<>();

            try {
                uuidOffender.loadAddresses().call();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // find alts
            for (String address : uuidOffender.getAddresses()) {
                Set<UUID> users = null;
                try {
                    users = PunishmentsPlugin.get().getStorage().getUsersForAddress(address).call();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (users == null) {
                    break;
                }

                for (UUID user : users) {
                    if (user.equals(target)) {
                        continue;
                    }

                    String username = null;
                    try {
                        username = PlayerLookups.getName(user).call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (username == null) {
                        break;
                    }

                    alts.add(username);
                }
            }

            if (!alts.isEmpty()) {
                caller.sendMessage(Messages.COLOR_MAIN + "This user has the following alts: " + Messages.COLOR_WARN + String.join(", ", alts));
            }
        });

        return true;
    }

}
