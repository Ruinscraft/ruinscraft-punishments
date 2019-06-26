package com.ruinscraft.punishments.behaviors;

import com.ruinscraft.punishments.Punishment;
import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentType;
import com.ruinscraft.punishments.util.Messages;

import java.util.StringJoiner;

public class KickBehavior implements KickablePunishmentBehavior {

    @Override
    public void perform(Punishment punishment, PunishmentAction action) {
        switch (action) {
            case CREATE:
                punishment.kickOffender(getKickMessage(punishment));
                break;
            case UNDO:
            case DELETE:
                punishment.sendMessageToOffender(Messages.COLOR_WARN + "A previous kick of yours has been deleted.");
                break;
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
