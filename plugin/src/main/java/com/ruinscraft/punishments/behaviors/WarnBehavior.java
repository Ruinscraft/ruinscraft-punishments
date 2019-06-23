package com.ruinscraft.punishments.behaviors;

import com.ruinscraft.punishments.Punishment;
import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentType;

public class WarnBehavior implements PunishmentBehavior {

    @Override
    public void punish(Punishment punishment, PunishmentAction action) {
        switch (action) {
            case CREATE:
                punishment.getOffenderPlayer().ifPresent(p -> p.sendMessage("You have been warned."));
            case UNDO:
            case DELETE:
                break; // TODO:
        }
        notify(punishment, PunishmentType.WARN, action);
    }

}
