package com.ruinscraft.punishments.dispatcher;

import com.ruinscraft.punishments.PunishmentEntry;
import com.ruinscraft.punishments.PunishmentType;

import java.util.HashMap;
import java.util.Map;

public class PunishmentDispatcher {

    private static final Map<PunishmentType, PunishmentHandler> _registry_;

    static {
        _registry_ = new HashMap<>();
        _registry_.put(PunishmentType.WARN, new WarnHandler());
    }


    public static void dispatch(PunishmentEntry entry) {
        // bcast to chat here, etc TODO:
        // redis message to all servers
        _registry_.get(entry.type).handle(entry.punishment);
    }

}
