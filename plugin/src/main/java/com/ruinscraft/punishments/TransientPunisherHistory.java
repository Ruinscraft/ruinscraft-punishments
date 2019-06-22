package com.ruinscraft.punishments;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TransientPunisherHistory {

    public static final UUID CONSOLE = new UUID(0,0);

    private static final Map<UUID, PunishmentEntry> history = new HashMap<>();

    public static PunishmentEntry getLast(UUID punisher) {
        return history.get(punisher);
    }

    public static void insert(PunishmentEntry entry) {
        history.put(entry.punishment.getPunisher(), entry);
    }

}
