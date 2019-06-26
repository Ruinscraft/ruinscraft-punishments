package com.ruinscraft.punishments.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DeletePunishmentCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 2:
                if (args[0].toLowerCase().equals("all")) {
                    return deleteAllPunishments(args[1]);
                } else {
                    return showHelp();
                }
            case 1:
                final int punishmentId;

                try {
                    punishmentId = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("Invalid punishment ID. Must be a number.");
                    return true;
                }

                return deletePunishment(punishmentId);
            default:
                return showHelp();
        }
    }

    private boolean showHelp() {
        return true;
    }

    private boolean deletePunishment(int punishmentId) {
        return true;
    }

    private boolean deleteAllPunishments(String offenderUsername) {
        return true;
    }

}
