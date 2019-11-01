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
                profile = PunishmentProfiles.getOrLoadProfile(getTarget(), IPOffender.class).join();
            } else {
                UUID targetUUID = PlayerLookups.getUniqueId(getTarget()).join();
                profile = PunishmentProfiles.getOrLoadProfile(targetUUID, UUIDOffender.class).join();
            }

            if (profile == null) {
                sender.sendMessage(Messages.COLOR_WARN + "Could not load Punishment Profile for " + getTarget());
                return;
            }

            profile.show(sender);
        });

        return true;
    }

}
