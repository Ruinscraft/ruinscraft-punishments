package com.ruinscraft.punishments.commands;

import com.ruinscraft.punishments.PunishmentsPlugin;
import com.ruinscraft.punishments.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DeletePunishmentCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Messages.COLOR_WARN + "/" + label + " <punishmentid>");
            return true;
        }

        int pId;

        try {
            pId = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Messages.COLOR_WARN + "Invalid punishmentid");
            return true;
        }

        PunishmentsPlugin.get().getStorage().delete(pId);
        sender.sendMessage(Messages.COLOR_MAIN + "Punishment with id " + pId + " has been deleted (if it existed).");
        return true;
    }

}
