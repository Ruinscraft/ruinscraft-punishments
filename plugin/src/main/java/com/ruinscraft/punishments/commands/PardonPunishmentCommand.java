package com.ruinscraft.punishments.commands;

import com.ruinscraft.punishments.*;
import com.ruinscraft.punishments.offender.IPOffender;
import com.ruinscraft.punishments.offender.UUIDOffender;
import com.ruinscraft.punishments.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PardonPunishmentCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("/" + label + " <username>");
            return true;
        }

        final boolean ip = label.endsWith("ip");
        final PunishmentType type = PunishmentType.match(label);

        if (type == null) {
            throw new IllegalStateException("PunishmentType was null");
        }

        if ((args[0].contains(".") || args[0].contains(":")) && !ip) {
            sender.sendMessage(Messages.COLOR_WARN + "Not a valid username. Did you mean to use /" + label.toLowerCase() + "ip?");
            return true;
        }

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

            Punishment active = profile.getActive(type);

            if (active == null) {
                sender.sendMessage(Messages.COLOR_WARN + args[0] + " is not " + type.getVerb() + ".");
                return;
            }

            active.setExpired();

            sender.sendMessage(Messages.COLOR_MAIN + "Un" + type.getVerb() + " " + args[0] + ".");

            PunishmentAction.PARDON.performRemote(PunishmentEntry.of(active, type));
        });

        return true;
    }

}
