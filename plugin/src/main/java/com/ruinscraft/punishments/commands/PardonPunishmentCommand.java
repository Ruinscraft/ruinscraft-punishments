package com.ruinscraft.punishments.commands;

import com.ruinscraft.punishments.*;
import com.ruinscraft.punishments.offender.UUIDOffender;
import com.ruinscraft.punishments.util.Messages;
import com.ruinscraft.punishments.util.Tasks;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class PardonPunishmentCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("/" + label + " <username>");
            return true;
        }

        PunishmentType type = PunishmentType.match(label);

        if (type == null) {
            throw new IllegalStateException("PunishmentType was null");
        }

        Tasks.async(() -> {
            final UUID target;

            try {
                target = PlayerLookups.getUniqueId(args[0]).call();
                args[0] = PlayerLookups.getName(target).call();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            if (target == null) {
                sender.sendMessage(Messages.COLOR_WARN + args[0] + " is not a valid Minecraft username.");
                return;
            }

            UUIDOffender uuidOffender = new UUIDOffender(target);

            final PunishmentProfile profile;

            try {
                profile = PunishmentProfile.getOrLoad(uuidOffender).call();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            if (profile == null) {
                sender.sendMessage(Messages.COLOR_WARN + "Profile for " + args[0] + " could not be loaded.");
                return;
            }

            Punishment active = profile.getActive(type);

            if (active == null) {
                sender.sendMessage(Messages.COLOR_WARN + args[0] + " is not " + type.getVerb() + ".");
                return;
            }

            active.setExpired();

            PunishmentAction.PARDON.call(PunishmentEntry.of(active, type));

            sender.sendMessage(Messages.COLOR_MAIN + "Un" + type.getVerb() + " " + args[0] + ".");
        });

        return true;
    }

}
