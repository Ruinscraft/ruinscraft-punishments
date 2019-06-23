package com.ruinscraft.punishments.messaging;

import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentEntry;

public interface MessageConsumer {

    default void consume(Message message) {
        final PunishmentEntry entry = message.datum;
        final PunishmentAction action = message.action;
        action.call(entry, false);
    }

}
