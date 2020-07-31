package com.ruinscraft.punishments.messaging;

import com.ruinscraft.punishments.*;

import java.util.Optional;

public interface MessageConsumer {

    default void consume(Message message) {
        String context = PunishmentsPlugin.get().getServerContext();

        if (!message.serverContext.equals(context)) {
            return;
        }

        final PunishmentEntry entry = message.datum;
        final PunishmentAction action = message.action;

        // adapt to local instance of the Offender
        {
            Optional<PunishmentProfile> profile = PunishmentProfiles.getProfile(entry.punishment.getOffender().getIdentifier());

            if (profile.isPresent()) {
                entry.punishment.setOffender(profile.get().getOffender());
            }
        }

        action.performLocal(entry);
    }

}
