package com.ruinscraft.punishments.commands;

import com.ruinscraft.punishments.*;
import com.ruinscraft.punishments.offender.IPOffender;
import com.ruinscraft.punishments.offender.UUIDOffender;
import com.ruinscraft.punishments.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PardonPunishmentCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("/" + label + " <username>");
            return true;
        }

        boolean ip = label.endsWith("ip");

        PunishmentType type = PunishmentType.match(label);

        if (type == null) {
            throw new IllegalStateException("PunishmentType was null");
        }

        if (ip) {
            PunishmentProfiles.getOrLoadProfile(args[0], IPOffender.class).thenAcceptAsync(profile -> {
                if (profile == null) {
                    sender.sendMessage(Messages.COLOR_WARN + "Profile for " + args[0] + " could not be loaded.");
                    return;
                }

                Punishment active = profile.getActive(type);

                if (active == null) {
                    sender.sendMessage(Messages.COLOR_WARN + args[0] + " is not " + type.getVerb() + ".");
                    return;
                }

                active.setExpired();

                PunishmentAction.PARDON.performRemote(PunishmentEntry.of(active, type)).thenRunAsync(() -> {
                    sender.sendMessage(Messages.COLOR_MAIN + "Un" + type.getVerb() + " " + args[0] + ".");
                });
            });
        } else {
            PlayerLookups.getUniqueId(args[0]).thenAcceptAsync(uuid -> {
                if (uuid == null) {
                    sender.sendMessage(Messages.COLOR_WARN + "Mojang UUID for " + args[0] + " not found.");
                    return;
                }

                PunishmentProfiles.getOrLoadProfile(uuid, UUIDOffender.class).thenAcceptAsync(profile -> {
                    if (profile == null) {
                        sender.sendMessage(Messages.COLOR_WARN + "Profile for " + args[0] + " could not be loaded.");
                        return;
                    }

                    Punishment active = profile.getActive(type);

                    if (active == null) {
                        sender.sendMessage(Messages.COLOR_WARN + args[0] + " is not " + type.getVerb() + ".");
                        return;
                    }

                    active.setExpired();

                    PunishmentAction.PARDON.performRemote(PunishmentEntry.of(active, type)).thenRunAsync(() -> {
                        sender.sendMessage(Messages.COLOR_MAIN + "Un" + type.getVerb() + " " + args[0] + ".");
                    });
                });
            });
        }

        return true;
    }

}
