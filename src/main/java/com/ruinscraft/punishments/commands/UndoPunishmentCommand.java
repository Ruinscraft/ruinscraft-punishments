package com.ruinscraft.punishments.commands;

import com.ruinscraft.punishments.*;
import com.ruinscraft.punishments.offender.IPOffender;
import com.ruinscraft.punishments.offender.UUIDOffender;
import com.ruinscraft.punishments.storage.Storage;
import com.ruinscraft.punishments.util.Messages;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UndoPunishmentCommand extends PunishmentCommandExecutor {

    private static Storage storage = PunishmentsPlugin.get().getStorage();

    public UndoPunishmentCommand() {
        super(true, false);
    }

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
    protected boolean run(CommandSender sender, String label, String[] args) {
        CompletableFuture.runAsync(() -> {
            final PunishmentProfile profile;

            if (isIp()) {
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
