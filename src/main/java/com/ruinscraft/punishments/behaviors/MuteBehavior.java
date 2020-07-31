package com.ruinscraft.punishments.behaviors;

import com.ruinscraft.punishments.Punishment;
import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentType;
import com.ruinscraft.punishments.offender.Offender;
import com.ruinscraft.punishments.offender.OnlineOffender;
import com.ruinscraft.punishments.util.Messages;

public class MuteBehavior implements PunishmentBehavior {

    @Override
    public void perform(Punishment punishment, PunishmentAction action) {
        Offender offender = punishment.getOffender();

        if (offender instanceof OnlineOffender) {
            OnlineOffender onlineOffender = (OnlineOffender) offender;

            switch (action) {
                case CREATE:
                    onlineOffender.sendMessage(Messages.COLOR_WARN + "You have been muted. Reason: " + punishment.getReason());
                    break;
                case PARDON:
                    onlineOffender.sendMessage(Messages.COLOR_WARN + "Your current mute has been pardoned.");
                    break;
                case DELETE:
                    onlineOffender.sendMessage(Messages.COLOR_WARN + "A previous mute of yours has been deleted.");
                    break;
            }
        }

        notifyServer(punishment, PunishmentType.MUTE, action);
    }

}
