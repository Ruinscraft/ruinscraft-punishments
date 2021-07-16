package com.ruinscraft.punishments.commands;

import com.ruinscraft.punishments.*;
import com.ruinscraft.punishments.offender.IPOffender;
import com.ruinscraft.punishments.offender.UUIDOffender;
import com.ruinscraft.punishments.storage.PunishmentStorage;
import com.ruinscraft.punishments.util.Messages;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UndoPunishmentCommand extends PunishmentCommandExecutor {

    private static PunishmentStorage storage = PunishmentsPlugin.get().getStorage();

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
                IPOffender ipOffender = new IPOffender(args[0]);
                profile = PunishmentProfiles.getOrLoadProfile(ipOffender).join();
            } else {
                UUID targetUUID = PlayerLookups.getUniqueId(args[0]).join();
                UUIDOffender uuidOffender = new UUIDOffender(targetUUID);
                profile = PunishmentProfiles.getOrLoadProfile(uuidOffender).join();
            }

            if (profile == null || !profile.hasPunishments()) {
                sender.sendMessage(Messages.COLOR_WARN + args[0] + " does not have any punishment history.");
                return;
            }

            storage.queryOffender(profile.getOffender()).thenAccept(entries -> {
                PunishmentEntry mostRecent = getMostRecent(entries);
                sender.sendMessage(Messages.COLOR_MAIN + "The " + mostRecent.type.getNoun() + " has been deleted.");
                mostRecent.performAction(PunishmentAction.DELETE, true);
            });
        });

        return true;
    }

}
