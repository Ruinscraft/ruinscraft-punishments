package com.ruinscraft.punishments.behaviors;

import com.ruinscraft.punishments.PunishmentType;

import java.util.HashMap;
import java.util.Map;

public class PunishmentBehaviorRegistry {

    protected static final Map<PunishmentType, PunishmentBehavior> _registry_ = new HashMap<>();

    static {
        _registry_.put(PunishmentType.WARN, new WarnBehavior());
        _registry_.put(PunishmentType.MUTE, new MuteBehavior());
        _registry_.put(PunishmentType.BAN, new BanBehavior());
    }

    public static PunishmentBehavior get(PunishmentType type) {
        return _registry_.get(type);
    }

}
