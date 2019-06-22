package com.ruinscraft.punishments.behaviors;

import com.ruinscraft.punishments.Punishment;

public class WarnBehavior implements PunishmentBehavior {

    @Override
    public void punish(Punishment punishment) {
        punishment.getOffenderPlayer().ifPresent((player) -> {
            player.sendMessage("You have been warned.");
        });
    }

}
