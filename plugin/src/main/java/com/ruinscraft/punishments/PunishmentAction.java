package com.ruinscraft.punishments;

import com.ruinscraft.punishments.messaging.Message;

public enum PunishmentAction {
    CREATE, UNDO, DELETE;

    public void sync(PunishmentEntry entry) {
        // save to db
        PunishmentsPlugin.get().getStorage().callAction(entry, this);
        // dispatch on messenger
        Message message = new Message(entry, this);
        PunishmentsPlugin.get().getMessageManager().getDispatcher().dispatch(message);
        // save to transient punisher history
        TransientPunisherHistory.insert(entry);
    }

}
