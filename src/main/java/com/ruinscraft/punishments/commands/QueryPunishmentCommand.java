package com.ruinscraft.punishments.commands;

import com.ruinscraft.punishments.PlayerLookups;
import com.ruinscraft.punishments.PunishmentProfile;
import com.ruinscraft.punishments.PunishmentProfiles;
import com.ruinscraft.punishments.offender.IPOffender;
import com.ruinscraft.punishments.offender.UUIDOffender;
import com.ruinscraft.punishments.util.Messages;
import org.bukkit.command.CommandSender;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class QueryPunishmentCommand extends PunishmentCommandExecutor {

    public QueryPunishmentCommand() {
        super(true, true);
    }

    @Override
    protected boolean run(CommandSender sender, String label, String[] args) {
        sender.sendMessage(Messages.COLOR_MAIN + "Looking up Punishment Profile for " + getTarget() + "...");

        CompletableFuture.runAsync(() -> {
            final PunishmentProfile profile;

            if (isIp()) {
                IPOffender ipOffender = new IPOffender(getTarget());
                profile = PunishmentProfiles.getOrLoadProfile(ipOffender).join();
            } else {
                UUID targetUUID = PlayerLookups.getUniqueId(getTarget()).join();
                UUIDOffender uuidOffender = new UUIDOffender(targetUUID);
                profile = PunishmentProfiles.getOrLoadProfile(uuidOffender).join();
            }

            if (profile == null) {
                sender.sendMessage(Messages.COLOR_WARN + "Could not load Punishment Profile for " + getTarget());
            } else {
                profile.showAll(sender);
            }
        });

        return true;
    }

}
