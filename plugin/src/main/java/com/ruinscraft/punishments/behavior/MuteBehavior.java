package com.ruinscraft.punishments.behavior;

import com.ruinscraft.punishments.Punishment;

public class MuteBehavior implements PunishmentBehavior {

    @Override
    public void punish(Punishment punishment) {
        punishment.getOffenderPlayer().ifPresent((player) -> {
            player.sendMessage("You have been muted.");
        });
    }

}
