package com.ruinscraft.punishments.commands;

import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentEntry;
import com.ruinscraft.punishments.TransientPunisherHistory;
import com.ruinscraft.punishments.console;
import com.ruinscraft.punishments.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UndoPunishmentCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final UUID punisher;

        if (sender instanceof Player) {
            punisher = ((Player) sender).getUniqueId();
        } else {
            punisher = console.UUID;
        }

        PunishmentEntry entry = TransientPunisherHistory.getLast(punisher);

        if (entry == null) {
            sender.sendMessage(Messages.COLOR_WARN + "No punishment history. Did the server reboot?");
            return true;
        }

        entry.call(PunishmentAction.DELETE);

        sender.sendMessage(Messages.COLOR_MAIN + "The " + entry.type.getNoun() + " has been deleted.");

        return true;
    }

}
