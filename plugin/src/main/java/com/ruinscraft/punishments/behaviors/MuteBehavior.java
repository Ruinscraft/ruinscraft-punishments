package com.ruinscraft.punishments.behaviors;

import com.ruinscraft.punishments.Punishment;
import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentType;
import com.ruinscraft.punishments.util.Messages;

public class MuteBehavior implements PunishmentBehavior {

    @Override
    public void perform(Punishment punishment, PunishmentAction action) {
        switch (action) {
            case CREATE:
                punishment.getOffenderPlayer().ifPresent(p ->
                        p.sendMessage(Messages.COLOR_WARN + "You have been muted. Reason: " + punishment.getReason()));
                break;
            case UNDO:
            case DELETE:
                punishment.getOffenderPlayer().ifPresent(p ->
                        p.sendMessage(Messages.COLOR_WARN + "A previous mute of yours has been deleted."));
                break;
        }
        notify(punishment, PunishmentType.MUTE, action);
    }

}
