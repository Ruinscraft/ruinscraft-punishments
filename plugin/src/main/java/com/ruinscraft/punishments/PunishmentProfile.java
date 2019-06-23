package com.ruinscraft.punishments;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class PunishmentProfile {

    private static final Map<UUID, PunishmentProfile> cache = new HashMap<>();

    public static PunishmentProfile get(UUID uuid) {
        return cache.get(uuid);
    }

    public static Callable<PunishmentProfile> load(UUID uuid) {
        return () -> {
            PunishmentProfile profile = new PunishmentProfile(uuid);
            profile.punishments = PunishmentsPlugin.get().getStorage().query(uuid).call();
            cache.put(uuid, profile);
            return profile;
        };
    }

    public static void unload(UUID uuid) {
        cache.remove(uuid);
    }

    private final UUID uuid;
    private List<PunishmentEntry> punishments;

    public PunishmentProfile(UUID uuid) {
        this.uuid = uuid;
        this.punishments = new ArrayList<>();
    }

    public boolean isMuted() {
        return getActive(PunishmentType.MUTE) != null;
    }

    public boolean isBanned() {
        return getActive(PunishmentType.BAN) != null;
    }

    public List<Punishment> getByType(PunishmentType type) {
        return punishments
                .stream()
                .filter(entry -> entry.type == type)
                .map(t -> t.punishment)
                .collect(Collectors.toList());
    }

    public Punishment getActive(PunishmentType type) {
        return getByType(type)
                .stream()
                .filter(p -> p.getExpirationTime() == -1L || (System.currentTimeMillis() < p.getExpirationTime()))
                .collect(Collectors.toList()).stream().findFirst().orElse(null);
    }

}
