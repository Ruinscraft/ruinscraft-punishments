package com.ruinscraft.punishments;

import com.ruinscraft.punishments.util.Messages;
import org.bukkit.command.CommandSender;

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

    public static Callable<PunishmentProfile> getOrLoad(UUID uuid) {
        return () -> {
            if (cache.containsKey(uuid)) {
                return cache.get(uuid);
            }
            return load(uuid).call();
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

    private static final String offset = "    ";

    public void show(CommandSender caller) {
        caller.sendMessage(Messages.COLOR_MAIN + "Kicks");
        for (Punishment kick : getByType(PunishmentType.KICK)) {
            caller.sendMessage(Messages.COLOR_WARN + offset + kick.getInceptionTimeFormatted() + " : " + kick.getReason());
        }
        caller.sendMessage(Messages.COLOR_MAIN + "Warns");
        for (Punishment warn : getByType(PunishmentType.WARN)) {
            caller.sendMessage(Messages.COLOR_WARN + offset + warn.getInceptionTimeFormatted() + " : " + warn.getReason());
        }
        caller.sendMessage(Messages.COLOR_MAIN + "Mutes");
        for (Punishment mute : getByType(PunishmentType.MUTE)) {
            caller.sendMessage(Messages.COLOR_WARN + offset + mute.getInceptionTimeFormatted() + " : " + mute.getReason());
        }
        caller.sendMessage(Messages.COLOR_MAIN + "Bans");
        for (Punishment ban : getByType(PunishmentType.BAN)) {
            caller.sendMessage(Messages.COLOR_WARN + offset + ban.getInceptionTimeFormatted() + " : " + ban.getReason());
        }
    }

}
