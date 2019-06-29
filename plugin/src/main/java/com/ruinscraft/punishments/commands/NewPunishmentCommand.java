package com.ruinscraft.punishments.commands;

import com.ruinscraft.punishments.*;
import com.ruinscraft.punishments.util.Duration;
import com.ruinscraft.punishments.util.Messages;
import com.ruinscraft.punishments.util.Tasks;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class NewPunishmentCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean temporary = label.toLowerCase().startsWith("temp");

        PunishmentType type = PunishmentType.match(label);

        if (type == null) {
            throw new IllegalStateException("PunishmentType was null");
        }

        int minArgs = 2 + (temporary ? 1 : 0);

        if (args.length < minArgs) {
            return showHelp(sender, label, temporary);
        }

        return createPunishment(sender, args, type, temporary);
    }

    private boolean showHelp(CommandSender sender, String label, boolean temporary) {
        String help = "/" + label + " <username>" + (temporary ? " <duration>" : "") + " <reason>";
        sender.sendMessage(help);
        return true;
    }

    private boolean createPunishment(CommandSender sender, String args[], PunishmentType type, boolean temporary) {
        final UUID target;

        try {
            target = PlayerLookups.getUniqueId(args[0]).call();
            args[0] = PlayerLookups.getName(target).call();
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }

        if (target == null) {
            sender.sendMessage(Messages.COLOR_WARN + args[0] + " is not a valid Minecraft username.");
            return true;
        }

        long duration = -1L;
        final String reason;

        if (temporary) {
            try {
                duration = Duration.getDurationFromWords(args[1]);
                reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            } catch (Exception e) {
                sender.sendMessage(Messages.COLOR_WARN + "Invalid duration.");
                return false;
            }
        } else {
            reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        }

        final UUID punisher;

        if (sender instanceof Player) {
            punisher = ((Player) sender).getUniqueId();
        } else {
            punisher = console.UUID;
        }

        final long finalDuration = duration;

        Tasks.async(() -> {
            try {
                PunishmentProfile targetProfile = PunishmentProfile.getOrLoad(target).call();

                if (targetProfile.getMostRecent() != null) {
                    long timeDiff = System.currentTimeMillis() - targetProfile.getMostRecent().punishment.getInceptionTime();
                    if (timeDiff < TimeUnit.SECONDS.toMillis(5)) {
                        sender.sendMessage(Messages.COLOR_WARN + "This user was just punished. Wait a few seconds.");
                        return;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            Punishment.builder()
                    .serverContext(PunishmentsPlugin.getServerContext())
                    .punisher(punisher)
                    .offender(target)
                    .offenderUsername(args[0])
                    .duration(finalDuration)
                    .reason(reason)
                    .build()
                    .entry(type)
                    .call(PunishmentAction.CREATE);
        });

        return true;
    }

}
