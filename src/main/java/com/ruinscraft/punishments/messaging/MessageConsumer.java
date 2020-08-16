package com.ruinscraft.punishments.messaging;

import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentEntry;
import com.ruinscraft.punishments.PunishmentProfile;
import com.ruinscraft.punishments.PunishmentProfiles;

import java.util.Optional;

public interface MessageConsumer {

    default void consume(Message message) {
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
