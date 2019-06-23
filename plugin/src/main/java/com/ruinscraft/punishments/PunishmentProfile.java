package com.ruinscraft.punishments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class PunishmentProfile {

    private static final Map<String, PunishmentProfile> cache = new HashMap<>();

    public static PunishmentProfile get(String username) {
        return cache.get(username);
    }

    public static Callable<PunishmentProfile> load(String username) {
        return () -> {
            PunishmentProfile profile = new PunishmentProfile(username);
            profile.punishments = PunishmentsPlugin.get().getStorage().query(username).call();
            return profile;
        };
    }

    private final String username;
    private List<PunishmentEntry> punishments;

    public PunishmentProfile(String username) {
        this.username = username;
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
                .filter(p -> p.getExpirationTime() == -1L || (System.currentTimeMillis() < p.getExpirationTime()))
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
