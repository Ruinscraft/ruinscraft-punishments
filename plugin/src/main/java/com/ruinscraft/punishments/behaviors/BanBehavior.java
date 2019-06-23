package com.ruinscraft.punishments.behaviors;

import com.ruinscraft.punishments.Punishment;
import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentType;

public class BanBehavior implements PunishmentBehavior {

    @Override
    public void perform(Punishment punishment, PunishmentAction action) {
        switch (action) {
            case CREATE:
                punishment.getOffenderPlayer().ifPresent(p ->
                        p.kickPlayer("You have been banned.\nReason: " + punishment.getReason()));
            case UNDO:
            case DELETE:
                break; // TODO:
        }
        notify(punishment, PunishmentType.BAN, action);
    }

}
