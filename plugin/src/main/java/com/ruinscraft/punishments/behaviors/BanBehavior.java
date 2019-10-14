package com.ruinscraft.punishments.behaviors;

import com.ruinscraft.punishments.Punishment;
import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentType;
import com.ruinscraft.punishments.PunishmentsPlugin;
import com.ruinscraft.punishments.offender.Offender;
import com.ruinscraft.punishments.util.Messages;

import java.util.StringJoiner;

public class BanBehavior implements KickablePunishmentBehavior {

    @Override
    public void perform(Punishment punishment, PunishmentAction action) {
        Offender offender = punishment.getOffender();

        switch (action) {
            case CREATE:
                offender.offerKick(getKickMessage(punishment));
                break;
            case PARDON:
                offender.offerChatMessage(Messages.COLOR_WARN + "Your current ban has been pardoned.");
                break;
            case DELETE:
                offender.offerChatMessage(Messages.COLOR_WARN + "A previous ban of yours has been deleted.");
                break;
        }

        notifyServer(punishment, PunishmentType.BAN, action);
    }

    @Override
    public String getKickMessage(Punishment punishment) {
        StringJoiner kickMsg = new StringJoiner("\n");
        kickMsg.add(Messages.COLOR_WARN + "You have been banned.");
        kickMsg.add("");
        kickMsg.add(Messages.COLOR_MAIN + "Reason: " + punishment.getReason());
        kickMsg.add("Expires in: " + punishment.getRemainingDurationWords());
        kickMsg.add("");
        kickMsg.add("Appeal @ " + PunishmentsPlugin.get().getConfig().getString("ban-appeal-link"));
        return kickMsg.toString();
    }

}
