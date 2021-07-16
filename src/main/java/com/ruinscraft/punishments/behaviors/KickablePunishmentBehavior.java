package com.ruinscraft.punishments.behaviors;

import com.ruinscraft.punishments.Punishment;

public abstract class KickablePunishmentBehavior extends PunishmentBehavior {

    public static final String APPEAL_LINK = "https://ruinscraft.com";

    public abstract String getKickMessage(Punishment punishment);

}
