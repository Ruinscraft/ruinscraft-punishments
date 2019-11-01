package com.ruinscraft.punishments.commands;

import com.ruinscraft.punishments.*;
import com.ruinscraft.punishments.offender.IPOffender;
import com.ruinscraft.punishments.offender.UUIDOffender;
import com.ruinscraft.punishments.storage.Storage;
import com.ruinscraft.punishments.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

        final boolean ip = label.endsWith("ip");

        if ((args[0].contains(".") || args[0].contains(":")) && !ip) {
            sender.sendMessage(Messages.COLOR_WARN + "Not a valid username. Did you mean to use /" + label.toLowerCase() + "ip?");
            return true;
        }

        CompletableFuture.runAsync(() -> {
            final PunishmentProfile profile;

            if (ip) {
                profile = PunishmentProfiles.getOrLoadProfile(args[0], IPOffender.class).join();
            } else {
                UUID targetUUID = PlayerLookups.getUniqueId(args[0]).join();
                profile = PunishmentProfiles.getOrLoadProfile(targetUUID, UUIDOffender.class).join();
            }

            if (profile == null || !profile.hasPunishments()) {
                sender.sendMessage(Messages.COLOR_WARN + args[0] + " does not have any punishment history.");
                return;
            }

            storage.queryOffender(profile.getOffender()).thenAccept(entries -> {
                PunishmentEntry mostRecent = getMostRecent(entries);

                if (!mostRecent.punishment.canBeUndone()) {
                    sender.sendMessage(Messages.COLOR_WARN + "This punishment is too old to undo.");
                    return;
                }

                sender.sendMessage(Messages.COLOR_MAIN + "The " + mostRecent.type.getNoun() + " has been deleted.");

                mostRecent.performAction(PunishmentAction.DELETE);
            });
        });
        return true;
    }

}
