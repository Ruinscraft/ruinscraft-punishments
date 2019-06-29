package com.ruinscraft.punishments.messaging;

import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentEntry;
import com.ruinscraft.punishments.PunishmentsPlugin;

public interface MessageConsumer {

    default void consume(Message message) {
        if (!message.serverContext.equals(PunishmentsPlugin.getServerContext())) {
            return;
        }
        final PunishmentEntry entry = message.datum;
        final PunishmentAction action = message.action;
        action.propagate(entry);
    }

}
