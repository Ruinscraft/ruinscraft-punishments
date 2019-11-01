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
import java.util.concurrent.CompletableFuture;

public class NewPunishmentCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final boolean temporary = label.toLowerCase().startsWith("temp");
        final boolean ip = label.toLowerCase().endsWith("ip");
        final PunishmentType type = PunishmentType.match(label);

        if (type == null) {
            throw new IllegalStateException("PunishmentType was null");
        }

        int minArgs = 2 + (temporary ? 1 : 0);

        if (args.length < minArgs) {
            String help = "/" + label + " <username>" + (temporary ? " <duration>" : "") + " <reason>";
            sender.sendMessage(help);
            return true;
        }

        if ((args[0].contains(".") || args[0].contains(":")) && !ip) {
            sender.sendMessage(Messages.COLOR_WARN + "Not a valid username. Did you mean to use /" + label.toLowerCase() + "ip?");
            return true;
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
            duration = Duration.getDurationFromWords(args[1]);
            reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        } else {
            duration = -1;
            reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
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

            if (profile.isAlready(type)) {
                sender.sendMessage(Messages.COLOR_WARN + args[0] + " is already " + type.getVerb() + ".");
                return;
            }

            if (profile.wasRecentlyPunished()) {
                sender.sendMessage(Messages.COLOR_WARN + "This address was just punished. Wait a few seconds.");
                return;
            }

            String context = PunishmentsPlugin.get().getServerContext();

            Punishment.builder()
                    .serverContext(context)
                    .punisher(punisher)
                    .offender(profile.getOffender())
                    .offenderUsername(args[0])
                    .punisherUsername(sender.getName())
                    .duration(duration)
                    .reason(reason)
                    .build()
                    .entry(type)
                    .performAction(PunishmentAction.CREATE);
        });

        return true;
    }

}
