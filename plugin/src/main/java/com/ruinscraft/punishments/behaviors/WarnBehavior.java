package com.ruinscraft.punishments.behaviors;

import com.ruinscraft.punishments.Punishment;
import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentType;
import com.ruinscraft.punishments.util.Messages;

public class WarnBehavior implements PunishmentBehavior {

    @Override
    public void perform(Punishment punishment, PunishmentAction action) {
        switch (action) {
            case CREATE:
                punishment.getOffenderPlayer().ifPresent(p ->
                        p.sendMessage(Messages.COLOR_WARN + "You have been warned. Reason: " + punishment.getReason()));
                break;
            case UNDO:
            case DELETE:
                punishment.getOffenderPlayer().ifPresent(p ->
                        p.sendMessage(Messages.COLOR_WARN + "A previous warn of yours has been deleted."));
                break;
        }
        notify(punishment, PunishmentType.WARN, action);
    }

}
