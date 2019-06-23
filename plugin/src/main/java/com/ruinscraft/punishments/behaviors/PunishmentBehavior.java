package com.ruinscraft.punishments.behaviors;

import com.ruinscraft.punishments.Punishment;
import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentType;
import org.bukkit.Bukkit;

import java.util.StringJoiner;

public interface PunishmentBehavior {

    void perform(Punishment punishment, PunishmentAction action);

    default void notify(Punishment punishment, PunishmentType type, PunishmentAction action) {
        StringJoiner joiner = new StringJoiner(" ");

        switch (action) {
            case CREATE:
                joiner.add(punishment.getOffenderUsername().orElse(punishment.getOffender()));
                joiner.add("has been");
                joiner.add(type.getVerb());
                joiner.add("for");
                joiner.add(punishment.getRemainingDurationWords());
                joiner.add("because");
                joiner.add(punishment.getReason());
                break;
            case UNDO:
            case DELETE:
                break;// TODO
        }

        Bukkit.broadcastMessage(joiner.toString());
    }

}
