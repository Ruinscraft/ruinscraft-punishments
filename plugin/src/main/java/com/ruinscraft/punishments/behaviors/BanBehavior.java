package com.ruinscraft.punishments.behaviors;

import com.ruinscraft.punishments.Punishment;

public class BanBehavior implements PunishmentBehavior {

    @Override
    public void punish(Punishment punishment) {
        punishment.getOffenderPlayer().ifPresent((player) -> {
            player.kickPlayer("You have been banned.");
        });
    }

}
