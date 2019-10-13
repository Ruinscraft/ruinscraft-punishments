package com.ruinscraft.punishments.commands;

import com.ruinscraft.punishments.console;
import com.ruinscraft.punishments.util.Messages;
import com.ruinscraft.punishments.util.Tasks;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UndoPunishmentCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Messages.COLOR_WARN + "No target specified.");
            return true;
        }

        final UUID punisher;

        if (sender instanceof Player) {
            punisher = ((Player) sender).getUniqueId();
        } else {
            punisher = console.UUID;
        }

        Tasks.async(() -> {

            

        });

        return true;
    }

}
