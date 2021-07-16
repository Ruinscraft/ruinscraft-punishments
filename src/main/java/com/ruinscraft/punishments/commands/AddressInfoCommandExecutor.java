package com.ruinscraft.punishments.commands;

import com.ruinscraft.punishments.AddressLog;
import com.ruinscraft.punishments.PunishmentsPlugin;
import com.ruinscraft.punishments.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

public class AddressInfoCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Messages.COLOR_MAIN + "/" + label + " <uuid/ip>");
            return true;
        }

        boolean username = true;

        if (args[0].contains(":") || args[0].contains(".") || args[0].contains("-")) {
            username = false;
        }

        if (username) {
            sender.sendMessage(Messages.COLOR_WARN + "Use a UUID or IP");
            sender.sendMessage(Messages.COLOR_WARN + "/" + label + " <uuid/ip>");
            return true;
        }

        boolean isUuid = args[0].length() == 36;

        if (isUuid) {
            UUID uuid;
            try {
                uuid = UUID.fromString(args[0]);
            } catch (Exception e) {
                sender.sendMessage(Messages.COLOR_WARN + "Invalid UUID.");
                return true;
            }
            PunishmentsPlugin.get().getStorage().queryAddressLogs(uuid).thenAccept(logs -> showAddressLogs(sender, logs, uuid.toString()));
        } else {
            String ip = args[0];
            PunishmentsPlugin.get().getStorage().queryAddressLogs(ip).thenAccept(logs -> showAddressLogs(sender, logs, ip));
        }

        return true;
    }

    private void showAddressLogs(CommandSender sender, List<AddressLog> logs, String lookup) {
        sender.sendMessage(Messages.COLOR_ACCENT + "Address logs for: " + lookup);
        for (AddressLog log : logs) {
            StringBuilder builder = new StringBuilder("  ");
            builder.append(log.getAddress() + " : " + log.getUsedAtFormatted() + " : " + log.getUsername());
            sender.sendMessage(Messages.COLOR_ACCENT + builder.toString());
        }
    }

}
