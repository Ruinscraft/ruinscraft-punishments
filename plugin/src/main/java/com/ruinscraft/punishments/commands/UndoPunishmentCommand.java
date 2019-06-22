package com.ruinscraft.punishments.commands;

import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentEntry;
import com.ruinscraft.punishments.TransientPunisherHistory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UndoPunishmentCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final UUID punisher;

        if (!(sender instanceof Player)) {
            punisher = TransientPunisherHistory.CONSOLE;
        } else {
            punisher = ((Player) sender).getUniqueId();
        }

        PunishmentEntry last = TransientPunisherHistory.getLast(punisher);

        if (last == null) {
            return true;
        }

        last.action(PunishmentAction.UNDO);

        return true;
    }

}
