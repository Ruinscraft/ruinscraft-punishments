package com.ruinscraft.punishments;

import com.ruinscraft.punishments.util.Messages;
import org.apache.commons.lang.WordUtils;
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
            for (PunishmentEntry entry : PunishmentsPlugin.get().getStorage().query(uuid).call()) {
                profile.punishments.put(entry.punishment.getPunishmentId(), entry);
            }
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
    private Map<Integer, PunishmentEntry> punishments;

    public PunishmentProfile(UUID uuid) {
        this.uuid = uuid;
        this.punishments = new HashMap<>();
    }

    public boolean isMuted() {
        return getActive(PunishmentType.MUTE) != null;
    }

    public boolean isBanned() {
        return getActive(PunishmentType.BAN) != null;
    }

    public List<Punishment> getByType(PunishmentType type) {
        return punishments
                .values()
                .stream()
                .filter(entry -> entry.type == type)
                .map(t -> t.punishment)
                .collect(Collectors.toList());
    }

    public Punishment getActive(PunishmentType type) {
        return getByType(type)
                .stream()
                .filter(p -> p.isInContext())
                .filter(p -> p.getExpirationTime() == -1L || (System.currentTimeMillis() < p.getExpirationTime()))
                .collect(Collectors.toList()).stream().findFirst().orElse(null);
    }

    public PunishmentEntry getMostRecent() {
        PunishmentEntry mostRecent = null;

        for (PunishmentEntry entry : punishments.values()) {
            if (mostRecent == null) {
                mostRecent = entry;
                continue;
            }

            if (entry.punishment.getInceptionTime() > mostRecent.punishment.getInceptionTime()) {
                mostRecent = entry;
            }
        }

        return mostRecent;
    }

    public boolean hasExcessiveAmount() {
        return punishments.size() > 25;
    }

    public void update(PunishmentEntry entry, PunishmentAction action) {
        switch (action) {
            case CREATE:
            case PARDON:
                punishments.put(entry.punishment.getPunishmentId(), entry);
                break;
            case DELETE:
                punishments.remove(entry.punishment.getPunishmentId());
                break;
        }
    }

    private static final String offset = "    ";

    public void show(CommandSender caller) {
        for (PunishmentType type : PunishmentType.values()) {
            final List<Punishment> punishments = getByType(type);
            boolean truncate = punishments.size() > 30;

            caller.sendMessage(Messages.COLOR_MAIN + WordUtils.capitalize(type.getPlural()) + " (" + punishments.size() + "):");

            if (truncate) {
                caller.sendMessage(Messages.COLOR_WARN + offset + "Too many to show (consider following the rules)");
                continue;
            }

            for (Punishment punishment : getByType(type)) {
                StringJoiner joiner = new StringJoiner(" ");

                joiner.add(Messages.COLOR_WARN + offset);
                joiner.add(punishment.getInceptionTimeFormatted());

                if (!punishment.getServerContext().equals("primary")) {
                    joiner.add("[" + punishment.getServerContext() + "]");
                }

                if (type.canBeTemporary()) {
                    joiner.add("[" + punishment.getTotalDurationWords() + "]");
                }

                joiner.add(":");
                joiner.add(punishment.getReason());

                caller.sendMessage(joiner.toString());
            }
        }
    }

}
