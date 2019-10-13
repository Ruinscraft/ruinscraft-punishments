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

    public void propagate(PunishmentEntry entry) {
        Tasks.sync(() -> {
            if (PunishmentProfile.get(entry.punishment.getOffender()) != null) {
                PunishmentProfile.get(entry.punishment.getOffender()).update(entry, this);
            }
            PunishmentBehaviorRegistry.get(entry.type).perform(entry.punishment, this);
        });
    }

}
