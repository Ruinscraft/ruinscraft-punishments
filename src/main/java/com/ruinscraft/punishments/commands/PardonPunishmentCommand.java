package com.ruinscraft.punishments.commands;

import com.ruinscraft.punishments.*;
import com.ruinscraft.punishments.offender.IPOffender;
import com.ruinscraft.punishments.offender.UUIDOffender;
import com.ruinscraft.punishments.util.Messages;
import org.bukkit.command.CommandSender;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PardonPunishmentCommand extends PunishmentCommandExecutor {

    public PardonPunishmentCommand() {
        super(true, false);
    }

    @Override
    protected boolean run(CommandSender sender, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("/" + label + " <username>");
            return true;
        }

        final PunishmentType type = PunishmentType.match(label);

        if (type == null) {
            throw new IllegalStateException("PunishmentType was null");
        }

        CompletableFuture.runAsync(() -> {
            PunishmentProfile profile;

            if (isIp()) {
                IPOffender ipOffender = new IPOffender(args[0]);
                profile = PunishmentProfiles.getOrLoadProfile(ipOffender).join();
            } else {
                UUID targetUUID = PlayerLookups.getUniqueId(args[0]).join();
                UUIDOffender uuidOffender = new UUIDOffender(targetUUID);
                profile = PunishmentProfiles.getOrLoadProfile(uuidOffender).join();
            }

            if (profile == null) {
                sender.sendMessage(Messages.COLOR_WARN + "Could not load Punishment Profile for " + args[0]);
                return;
            }

            Punishment active = profile.getActive(type);

            if (active == null) {
                sender.sendMessage(Messages.COLOR_WARN + args[0] + " is not " + type.getVerb() + ".");
                return;
            }

            active.setExpired();
            active.entry(type).performAction(PunishmentAction.PARDON, true);

            sender.sendMessage(Messages.COLOR_MAIN + "Un" + type.getVerb() + " " + args[0] + ".");
        });

        return true;
    }

}
