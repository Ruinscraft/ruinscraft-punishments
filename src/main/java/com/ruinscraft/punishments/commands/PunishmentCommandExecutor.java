package com.ruinscraft.punishments.commands;

import com.ruinscraft.punishments.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class PunishmentCommandExecutor implements CommandExecutor {

    private boolean requiresTarget;
    private boolean targetCanBeSelf;
    private boolean temporary;
    private boolean ip;
    private String target;

    public PunishmentCommandExecutor(boolean requiresTarget, boolean targetCanBeSelf) {
        this.requiresTarget = requiresTarget;
        this.targetCanBeSelf = targetCanBeSelf;
    }

    protected boolean requiresTarget() {
        return requiresTarget;
    }

    protected boolean targetCanBeSelf() {
        return targetCanBeSelf;
    }

    protected boolean isTemporary() {
        return temporary;
    }

    protected boolean isIp() {
        return ip;
    }

    protected String getTarget() {
        return target;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        temporary = label.startsWith("temp");
        ip = label.endsWith("ip");

        if (requiresTarget) {
            if (args.length == 0 && targetCanBeSelf && sender instanceof Player) {
                if (ip) {
                    target = ((Player) sender).getAddress().getHostString();
                } else {
                    target = sender.getName();
                }
            } else if ((args.length == 0 && !targetCanBeSelf) || (args.length == 0 && !(sender instanceof Player))) {
                sender.sendMessage(Messages.COLOR_WARN + "No target specified");
                return true;
            } else {
                target = args[0];
            }

            if ((target.contains(".") || target.contains(":")) && !ip) {
                sender.sendMessage(Messages.COLOR_WARN + "Not a valid username. Did you mean to use /" + label.toLowerCase() + "ip?");
                return true;
            }
        }

        return run(sender, label, args);
    }

    protected abstract boolean run(CommandSender sender, String label, String args[]);

}
