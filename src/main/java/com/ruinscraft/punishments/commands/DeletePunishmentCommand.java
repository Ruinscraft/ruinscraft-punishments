package com.ruinscraft.punishments.commands;

import org.bukkit.command.CommandSender;

public class DeletePunishmentCommand extends PunishmentCommandExecutor {

    public DeletePunishmentCommand() {
        super(true, false);
    }

    @Override
    protected boolean run(CommandSender sender, String label, String[] args) {
        sender.sendMessage("Not implemented yet");
        return true;
    }

}
