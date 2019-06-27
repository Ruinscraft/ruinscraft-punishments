package com.ruinscraft.punishments;

import com.ruinscraft.punishments.behaviors.PunishmentBehaviorRegistry;
import com.ruinscraft.punishments.messaging.Message;
import com.ruinscraft.punishments.util.Tasks;

public enum PunishmentAction {
    CREATE, PARDON, DELETE;

    public void call(PunishmentEntry entry) {
        Tasks.async(() -> {
            // save to db
            try {
                PunishmentsPlugin.get().getStorage().callAction(entry, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // dispatch on messenger
            Message message = new Message(entry, this);
            PunishmentsPlugin.get().getMessageManager().getDispatcher().dispatch(message);
        });
    }

    public void propegate(PunishmentEntry entry) {
        Tasks.sync(() -> {
            TransientPunisherHistory.insert(entry);
            PunishmentBehaviorRegistry.get(entry.type).perform(entry.punishment, this);
        });
    }

}
