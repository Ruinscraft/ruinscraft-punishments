package com.ruinscraft.punishments.behaviors;

import com.ruinscraft.punishments.Punishment;
import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentType;
import com.ruinscraft.punishments.util.Messages;
import org.bukkit.Bukkit;

import java.util.StringJoiner;

public interface PunishmentBehavior {

    void perform(Punishment punishment, PunishmentAction action);

    default void notifyServer(Punishment punishment, PunishmentType type, PunishmentAction action) {
        StringJoiner joiner = new StringJoiner(" ");

        switch (action) {
            case CREATE:
                joiner.add(Messages.COLOR_WARN + punishment.getOffenderUsername());
                joiner.add("has been");
                joiner.add(type.getVerb());
                joiner.add("for");
                joiner.add(punishment.getReason());
                if (punishment.isTemporary()) {
                    joiner.add("Expires in:");
                    joiner.add(punishment.getRemainingDurationWords());
                }
                break;
            case PARDON:
            case DELETE:
                break;
        }

        Bukkit.broadcastMessage(joiner.toString());
    }

}
