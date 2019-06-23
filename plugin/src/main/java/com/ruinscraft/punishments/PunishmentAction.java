package com.ruinscraft.punishments;

import com.ruinscraft.punishments.behaviors.PunishmentBehaviorRegistry;
import com.ruinscraft.punishments.messaging.Message;
import com.ruinscraft.punishments.util.Tasks;

public enum PunishmentAction {
    CREATE, UNDO, DELETE;

    public void call(PunishmentEntry entry, boolean origin) {
        Tasks.async(() -> {
            if (origin) {
                // save to db
                try {
                    PunishmentsPlugin.get().getStorage().callAction(entry, this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // dispatch on messenger
                Message message = new Message(entry, this);
                PunishmentsPlugin.get().getMessageManager().getDispatcher().dispatch(message);
            }

            // save to transient punisher history
            TransientPunisherHistory.insert(entry);
            // enact punishment behavior
            PunishmentBehaviorRegistry.get(entry.type).punish(entry.punishment, this);
        });
    }

}
