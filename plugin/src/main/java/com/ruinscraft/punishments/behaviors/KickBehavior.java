package com.ruinscraft.punishments.behaviors;

import com.ruinscraft.punishments.Punishment;
import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentType;
import com.ruinscraft.punishments.util.Messages;

import java.util.StringJoiner;

public class KickBehavior implements PunishmentBehavior {

    @Override
    public void perform(Punishment punishment, PunishmentAction action) {
        switch (action) {
            case CREATE:
                punishment.getOffenderPlayer().ifPresent(p -> p.kickPlayer(getKickMessage(punishment)));
                break;
            case UNDO:
            case DELETE:
                punishment.getOffenderPlayer().ifPresent(p ->
                        p.sendMessage(Messages.COLOR_WARN + "A previous kick of yours has been deleted."));
                break;
        }
        notify(punishment, PunishmentType.KICK, action);
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
