package com.ruinscraft.punishments.commands;

import com.ruinscraft.punishments.*;
import com.ruinscraft.punishments.offender.UUIDOffender;
import com.ruinscraft.punishments.storage.Storage;
import com.ruinscraft.punishments.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class UndoPunishmentCommand implements CommandExecutor {

    private static Storage storage = PunishmentsPlugin.get().getStorage();

    // TODO: maybe move this to a util class?
    private static PunishmentEntry getMostRecent(List<PunishmentEntry> entries) {
        if (entries.isEmpty()) return null;

        PunishmentEntry mostRecent = entries.get(0);

        for (PunishmentEntry entry : entries) {
            if (entry.punishment.getInceptionTime() < mostRecent.punishment.getInceptionTime()) {
                mostRecent = entry;
            }
        }

        return mostRecent;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Messages.COLOR_WARN + "No target specified.");
            return true;
        }

        PlayerLookups.getUniqueId(args[0]).thenAcceptAsync(uuid -> {
            if (uuid == null) {
                sender.sendMessage(Messages.COLOR_WARN + args[0] + " is not a valid Minecraft username.");
                return;
            }

            PunishmentProfiles.getOrLoadProfile(uuid, UUIDOffender.class).thenAcceptAsync(profile -> {
                if (profile == null || !profile.hasPunishments()) {
                    sender.sendMessage(Messages.COLOR_WARN + args[0] + " does not have any punishment history.");
                    return;
                }

                storage.queryOffender(profile.getOffender()).thenAcceptAsync(entries -> {
                    PunishmentEntry mostRecent = getMostRecent(entries);

                    if (!mostRecent.punishment.canBeUndone()) {
                        sender.sendMessage(Messages.COLOR_WARN + "This punishment is too old to undo.");
                        return;
                    }

                    mostRecent.call(PunishmentAction.DELETE).thenRunAsync(() -> {
                        sender.sendMessage(Messages.COLOR_MAIN + "The " + mostRecent.type.getNoun() + " has been deleted.");
                    });
                });
            });
        });

        return true;
    }

}
