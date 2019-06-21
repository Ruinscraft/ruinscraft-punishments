package com.ruinscraft.punishments.dispatcher;

import com.ruinscraft.punishments.Punishment;
import com.ruinscraft.punishments.PunishmentType;

import java.util.HashMap;
import java.util.Map;

public class PunishmentDispatcher {

    private static final Map<PunishmentType, PunishmentHandler> _registry_;

    static {
        _registry_ = new HashMap<>();
        _registry_.put(PunishmentType.WARN, new WarnHandler());
    }


    public static void dispatch(PunishmentType type, Punishment punishment) {
        // bcast to chat here, etc TODO:
        _registry_.get(type).handle(punishment);
    }

}
