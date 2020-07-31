package com.ruinscraft.punishments.messaging;

import com.google.gson.Gson;
import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentEntry;
import com.ruinscraft.punishments.PunishmentsPlugin;

import java.util.UUID;

public class Message {

    private static final Gson GSON = new Gson();

    public final UUID messageId;
    public final String serverContext;
    public final PunishmentEntry datum;
    public final PunishmentAction action;

    public Message(PunishmentEntry datum, PunishmentAction action) {
        this.messageId = UUID.randomUUID();
        this.serverContext = PunishmentsPlugin.get().getConfig().getString("server-context");
        this.datum = datum;
        this.action = action;
    }

    public String serialize() {
        return GSON.toJson(this);
    }

}
