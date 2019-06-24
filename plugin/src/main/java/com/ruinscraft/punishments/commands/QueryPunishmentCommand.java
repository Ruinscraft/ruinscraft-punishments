package com.ruinscraft.punishments.commands;

import com.ruinscraft.punishments.PunishmentProfile;
import com.ruinscraft.punishments.mojang.AccountsAPI;
import com.ruinscraft.punishments.util.Messages;
import com.ruinscraft.punishments.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.UUID;

public class QueryPunishmentCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("/" + label + " <username>");
            return true;
        }

        sender.sendMessage(Messages.COLOR_MAIN + "Looking up punishments profile for " + args[0] + "...");

        Tasks.async(() -> {
            final UUID target;
            OfflinePlayer offlineTargetPlayer = Bukkit.getOfflinePlayer(args[0]);

            if (offlineTargetPlayer.hasPlayedBefore()) {
                target = offlineTargetPlayer.getUniqueId();
            } else {
                try {
                    AccountsAPI.AccountsProfile profile = AccountsAPI.getAccountsProfile(args[0]);
                    if (profile == null) {
                        sender.sendMessage(Messages.COLOR_WARN + "Player with username " + args[0] + " doesn't exist.");
                        return;
                    }
                    target = profile.getUniqueId();
                } catch (IOException e) {
                    sender.sendMessage(Messages.COLOR_WARN + "Error while looking up " + args[0]);
                    e.printStackTrace();
                    return;
                }
            }

            try {
                PunishmentProfile profile = PunishmentProfile.getOrLoad(target).call();
                profile.show(sender);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return true;
    }

}
