package com.ruinscraft.punishments.behaviors;

import com.ruinscraft.punishments.Punishment;
import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentType;
import com.ruinscraft.punishments.offender.Offender;
import com.ruinscraft.punishments.offender.OnlineOffender;
import com.ruinscraft.punishments.util.Messages;

import java.util.StringJoiner;

public class KickBehavior implements KickablePunishmentBehavior {

    @Override
    public void perform(Punishment punishment, PunishmentAction action) {
        Offender offender = punishment.getOffender();

        if (offender instanceof OnlineOffender) {
            OnlineOffender onlineOffender = (OnlineOffender) offender;

            switch (action) {
                case CREATE:
                    onlineOffender.kick(getKickMessage(punishment));
                    break;
                case DELETE:
                    onlineOffender.sendMessage(Messages.COLOR_WARN + "A previous kick of yours has been deleted.");
                    break;
            }
        }

        notifyServer(punishment, PunishmentType.KICK, action);
    }

    @Override
    public String getKickMessage(Punishment punishment) {
        StringJoiner kickMsg = new StringJoiner("\n");
        kickMsg.add(Messages.COLOR_WARN + "You have been kicked.");
        kickMsg.add("");
        kickMsg.add(Messages.COLOR_MAIN + "Reason: " + punishment.getReason());
        return kickMsg.toString();
    }

}
