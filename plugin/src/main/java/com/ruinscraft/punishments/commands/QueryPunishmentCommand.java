package com.ruinscraft.punishments.commands;

import com.ruinscraft.punishments.util.Messages;
import com.ruinscraft.punishments.util.Tasks;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class QueryPunishmentCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("/" + label + " <username/uuid>");
            return true;
        }

        sender.sendMessage(Messages.COLOR_MAIN + "Looking up punishments profile for " + args[0] + "...");

        Tasks.async(() -> {









        });

        return true;
    }

}
