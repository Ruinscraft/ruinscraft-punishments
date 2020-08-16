package com.ruinscraft.punishments.commands;

import org.bukkit.command.CommandSender;

public class RevisePunishmentCommand extends PunishmentCommandExecutor {

    public RevisePunishmentCommand(boolean requiresTarget, boolean targetCanBeSelf) {
        super(requiresTarget, targetCanBeSelf);
    }

    @Override
    protected boolean run(CommandSender sender, String label, String[] args) {
        return false;
    }

}
