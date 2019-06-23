package com.ruinscraft.punishments;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PunishmentProfile {

    private final UUID uuid;
    protected List<PunishmentEntry> punishments;

    public PunishmentProfile(UUID uuid) {
        this.uuid = uuid;
        this.punishments = new ArrayList<>();
    }

    public boolean isMuted() {
        return !isExpired(PunishmentType.MUTE);
    }

    public boolean isBanned() {
        return !isExpired(PunishmentType.BAN);
    }

    private boolean isExpired(PunishmentType type) {
        return getByType(type)
                .stream()
                .filter(p -> System.currentTimeMillis() < p.getExpirationTime())
                .collect(Collectors.toList())
                .isEmpty();
    }

    public List<Punishment> getByType(PunishmentType type) {
        return punishments
                .stream()
                .filter(entry -> entry.type == type)
                .map(t -> t.punishment)
                .collect(Collectors.toList());
    }

}
