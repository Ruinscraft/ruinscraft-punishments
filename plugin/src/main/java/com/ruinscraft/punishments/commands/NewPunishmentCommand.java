package com.ruinscraft.punishments.commands;

import com.ruinscraft.punishments.*;
import com.ruinscraft.punishments.offender.IPOffender;
import com.ruinscraft.punishments.offender.UUIDOffender;
import com.ruinscraft.punishments.util.Duration;
import com.ruinscraft.punishments.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

public class NewPunishmentCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean temporary = label.toLowerCase().startsWith("temp");
        boolean ip = label.toLowerCase().endsWith("ip");

        PunishmentType type = PunishmentType.match(label);

        if (type == null) {
            throw new IllegalStateException("PunishmentType was null");
        }

        int minArgs = 2 + (temporary ? 1 : 0);

        if (args.length < minArgs) {
            return showHelp(sender, label, temporary);
        }

        final UUID punisher;

        if (sender instanceof Player) {
            punisher = ((Player) sender).getUniqueId();
        } else {
            punisher = console.UUID;
        }

        final long duration;
        final String reason;

        if (temporary) {
            try {
                duration = Duration.getDurationFromWords(args[1]);
                reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            } catch (Exception e) {
                sender.sendMessage(Messages.COLOR_WARN + "Invalid duration.");
                return true;
            }
        } else {
            duration = -1;
            reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        }

        if (ip) {
            PunishmentProfiles.getOrLoadProfile(args[0], IPOffender.class).thenAcceptAsync(profile -> {
                if (profile.isAlready(type)) {
                    sender.sendMessage(Messages.COLOR_WARN + args[0] + " is already " + type.getVerb() + ".");
                    return;
                }

                if (profile.wasRecentlyPunished()) {
                    sender.sendMessage(Messages.COLOR_WARN + "This address was just punished. Wait a few seconds.");
                    return;
                }

                Punishment.builder()
                        .serverContext(PunishmentsPlugin.getServerContext())
                        .punisher(punisher)
                        .offender(profile.getOffender())
                        .offenderUsername(args[0])
                        .duration(duration)
                        .reason(reason)
                        .build()
                        .entry(type)
                        .call(PunishmentAction.CREATE);
            });
        } else {
            PlayerLookups.getUniqueId(args[0]).thenAcceptAsync(uuid -> {
                if (uuid == null) {
                    sender.sendMessage(Messages.COLOR_WARN + "Mojang UUID for " + args[0] + " not found.");
                    return;
                }

                PunishmentProfiles.getOrLoadProfile(uuid, UUIDOffender.class).thenAcceptAsync(profile -> {
                    if (profile.isAlready(type)) {
                        sender.sendMessage(Messages.COLOR_WARN + args[0] + " is already " + type.getVerb() + ".");
                        return;
                    }

                    if (profile.wasRecentlyPunished()) {
                        sender.sendMessage(Messages.COLOR_WARN + "This user was just punished. Wait a few seconds.");
                        return;
                    }

                    Punishment.builder()
                            .serverContext(PunishmentsPlugin.getServerContext())
                            .punisher(punisher)
                            .offender(profile.getOffender())
                            .offenderUsername(args[0])
                            .duration(duration)
                            .reason(reason)
                            .build()
                            .entry(type)
                            .call(PunishmentAction.CREATE);
                });
            });
        }

        return true;
    }

    private boolean showHelp(CommandSender sender, String label, boolean temporary) {
        String help = "/" + label + " <username>" + (temporary ? " <duration>" : "") + " <reason>";
        sender.sendMessage(help);
        return true;
    }

}
