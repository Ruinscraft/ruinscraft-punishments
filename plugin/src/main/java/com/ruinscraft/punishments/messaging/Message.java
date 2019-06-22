package com.ruinscraft.punishments.messaging;

import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentEntry;

import java.util.UUID;

public class Message {

    public final UUID messageId;
    public final PunishmentEntry datum;
    public final PunishmentAction action;

    public Message(PunishmentEntry datum, PunishmentAction action) {
        this.messageId = UUID.randomUUID();
        this.datum = datum;
        this.action = action;
    }

}
