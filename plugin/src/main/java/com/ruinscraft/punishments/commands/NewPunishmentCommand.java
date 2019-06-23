package com.ruinscraft.punishments.commands;

import com.ruinscraft.punishments.Punishment;
import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentType;
import com.ruinscraft.punishments.console;
import com.ruinscraft.punishments.mojang.AccountsAPI;
import com.ruinscraft.punishments.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Arrays;
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
            throw new IllegalStateException("PunishmentType was null");
        }

        if (args.length >= 3) {
            if (!temporary) {
                return showHelp(sender, label, false);
            } else {
                return createPunishment(sender, args, type, true);
            }
        } else if (args.length >= 2) {
            if (temporary) {
                return showHelp(sender, label, true);
            } else {
                return createPunishment(sender, args, type, false);
            }
        }

        return showHelp(sender, label, temporary);
    }

    private boolean showHelp(CommandSender sender, String label, boolean temporary) {
        String help = "/" + label + " <username>" + (temporary ? " <duration>" : "") + " <reason>";
        sender.sendMessage(help);
        return true;
    }

    private boolean createPunishment(CommandSender sender, String args[], PunishmentType type, boolean temporary) {
        final UUID offender;

        OfflinePlayer offlineOffenderPlayer = Bukkit.getOfflinePlayer(args[0]);

        if (offlineOffenderPlayer.hasPlayedBefore()) {
            offender = offlineOffenderPlayer.getUniqueId();
        } else {
            try {
                AccountsAPI.AccountsProfile profile = AccountsAPI.getAccountsProfile(args[0]);
                if (profile == null) {
                    sender.sendMessage(Messages.COLOR_WARN + "Player with username " + args[0] + " does not exist.");
                    return true;
                }
                offender = profile.getUniqueId();
                args[0] = profile.getName();
            } catch (IOException e) {
                e.printStackTrace();
                sender.sendMessage("Error while looking up username.");
                return false;
            }
        }

        long duration = -1L;
        final String reason;

        if (temporary) {
            try {
                duration = Long.parseLong(args[1]);
                reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid duration.");
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

        Punishment.builder()
                .punisher(punisher)
                .offender(offender)
                .offenderUsername(args[0])
                .duration(duration)
                .reason(reason)
                .build()
                .entry(type)
                .call(PunishmentAction.CREATE);

        return true;
    }

}
