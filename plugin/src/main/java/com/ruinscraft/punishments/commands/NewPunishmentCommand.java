package com.ruinscraft.punishments.commands;

import com.ruinscraft.punishments.Punishment;
import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentType;
import com.ruinscraft.punishments.console;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NewPunishmentCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean temporary = label.toLowerCase().startsWith("temp");

        PunishmentType type = null;

        for (PunishmentType _type : PunishmentType.values()) {
            if (label.toUpperCase().contains(_type.name())) {
                type = _type;
            }
        }

        if (type == null) {
            return false; // should never happen...
        }

        switch (args.length) {
            case 2:
                if (temporary) {
                    return showHelp(sender, label, true);
                } else {
                    return createPunishment(sender, args, type, false);
                }
            case 3:
                if (!temporary) {
                    return showHelp(sender, label, false);
                } else {
                    return createPunishment(sender, args, type, true);
                }
            default:
                return showHelp(sender, label, temporary);
        }
    }

    private boolean showHelp(CommandSender sender, String label, boolean temporary) {
        String help = "/" + label + " <player>" + (temporary ? " <duration>" : "") + " <reason>";
        sender.sendMessage(help);
        return true;
    }

    private boolean createPunishment(CommandSender sender, String args[], PunishmentType type, boolean temporary) {
        String offender = args[0];
        long duration = -1L;
        final String reason;

        if (temporary) {
            try {
                duration = Long.parseLong(args[1]);
                reason = args[2];
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid duration.");
                return false;
            }
        } else {
            reason = args[1];
        }

        final UUID punisher;

        if (sender instanceof Player) {
            punisher = ((Player) sender).getUniqueId();
        } else {
            punisher = console.UUID;
        }

        Punishment.builder()
                .punisher(punisher)
                .offender(offender)
                .duration(duration)
                .reason(reason)
                .build()
                .entry(type)
                .perform(PunishmentAction.CREATE, true);

        return true;
    }

}
