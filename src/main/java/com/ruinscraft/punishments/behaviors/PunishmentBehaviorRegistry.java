package com.ruinscraft.punishments.behaviors;

import com.ruinscraft.punishments.PunishmentType;

import java.util.HashMap;
import java.util.Map;

public class PunishmentBehaviorRegistry {

    protected static final Map<PunishmentType, PunishmentBehavior> REGISTRY = new HashMap<>();

    static {
        REGISTRY.put(PunishmentType.KICK, new KickBehavior());
        REGISTRY.put(PunishmentType.WARN, new WarnBehavior());
        REGISTRY.put(PunishmentType.MUTE, new MuteBehavior());
        REGISTRY.put(PunishmentType.BAN, new BanBehavior());
    }

    public static PunishmentBehavior get(PunishmentType type) {
        return REGISTRY.get(type);
    }

}
