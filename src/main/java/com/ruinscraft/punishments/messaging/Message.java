package com.ruinscraft.punishments.messaging;

import com.google.gson.Gson;
import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentEntry;

import java.util.UUID;

public class Message {

    private static final Gson GSON = new Gson();

    public final UUID messageId;
    public final PunishmentEntry datum;
    public final PunishmentAction action;

    public Message(PunishmentEntry datum, PunishmentAction action) {
        this.messageId = UUID.randomUUID();
        this.datum = datum;
        this.action = action;
    }

    public String serialize() {
        return GSON.toJson(this);
    }

}
