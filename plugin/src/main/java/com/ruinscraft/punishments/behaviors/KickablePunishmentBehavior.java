package com.ruinscraft.punishments.behaviors;

import com.ruinscraft.punishments.Punishment;

public interface KickablePunishmentBehavior extends PunishmentBehavior {

    String getKickMessage(Punishment punishment);

}
