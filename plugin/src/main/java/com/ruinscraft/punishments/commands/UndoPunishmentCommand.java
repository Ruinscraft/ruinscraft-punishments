package com.ruinscraft.punishments.commands;

import com.ruinscraft.punishments.PlayerLookups;
import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentEntry;
import com.ruinscraft.punishments.PunishmentsPlugin;
import com.ruinscraft.punishments.util.Messages;
import com.ruinscraft.punishments.util.Tasks;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class UndoPunishmentCommand implements CommandExecutor {

    private static final long MAX_UNDO_TIME = TimeUnit.DAYS.toMillis(3);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Messages.COLOR_WARN + "No target specified.");
            return true;
        }

        Tasks.async(() -> {
            UUID offender;

            try {
                offender = PlayerLookups.getUniqueId(args[0]).call();
            } catch (Exception e) {
                sender.sendMessage(Messages.COLOR_WARN + "There was an error. Please notify an admin.");

                e.printStackTrace();

                return;
            }

            if (offender == null) {
                sender.sendMessage(Messages.COLOR_WARN + "Target not found.");

                return;
            }

            List<PunishmentEntry> entries;

            try {
                entries = PunishmentsPlugin.get().getStorage().queryOffender(offender).call();
            } catch (Exception e) {
                sender.sendMessage(Messages.COLOR_WARN + "There was an error. Please notify an admin.");

                e.printStackTrace();

                return;
            }

            PunishmentEntry mostRecent = getMostRecent(entries);

            if (mostRecent == null) {
                sender.sendMessage(Messages.COLOR_WARN + "Target has no punishments.");

                return;
            }

            if (mostRecent.punishment.getInceptionTime() + MAX_UNDO_TIME < System.currentTimeMillis()) {
                sender.sendMessage(Messages.COLOR_WARN + "This punishment is too old to undo.");

                return;
            }

            mostRecent.call(PunishmentAction.DELETE);

            sender.sendMessage(Messages.COLOR_MAIN + "The " + mostRecent.type.getNoun() + " has been deleted.");
        });

        return true;
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

}
