package com.ruinscraft.punishments.commands;

import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentEntry;
import com.ruinscraft.punishments.TransientPunisherHistory;
import com.ruinscraft.punishments.console;
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
            punisher = console.UUID;
        } else {
            punisher = ((Player) sender).getUniqueId();
        }

        PunishmentEntry last = TransientPunisherHistory.getLast(punisher);

        if (last == null) {
            return true;
        }

        last.call(PunishmentAction.UNDO);

        return true;
    }

}
