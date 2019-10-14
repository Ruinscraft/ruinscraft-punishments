package com.ruinscraft.punishments;

import com.ruinscraft.punishments.offender.Offender;
import com.ruinscraft.punishments.util.Messages;
import org.apache.commons.lang.WordUtils;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PunishmentProfile {

    /* cache =========================================================================================== */
    private static final Map<Offender, PunishmentProfile> cache = new ConcurrentHashMap<>();

    public static <OFFENDERIDENTIFIER> PunishmentProfile get(OFFENDERIDENTIFIER offenderidentifier) {
        for (Offender offender : cache.keySet()) {
            if (offender.getIdentifier().equals(offenderidentifier)) {
                return cache.get(offender);
            }
        }
        return null;
    }

    public static Callable<PunishmentProfile> load(Offender offender) {
        return () -> {
            PunishmentProfile profile = new PunishmentProfile(offender);
            for (PunishmentEntry entry : PunishmentsPlugin.get().getStorage().queryOffender(offender).call()) {
                profile.punishments.put(entry.punishment.getPunishmentId(), entry);
            }
            cache.put(offender, profile);
            System.out.println("CACHE SIZE: " + cache.size());
            return profile;
        };
    }

    public static Callable<PunishmentProfile> getOrLoad(Offender offender) {
        return () -> {
            if (cache.containsKey(offender)) {
                return cache.get(offender);
            }
            return load(offender).call();
        };
    }

    public static <OFFENDERIDENTIFIER> void unload(OFFENDERIDENTIFIER offenderidentifier) {
        PunishmentProfile punishmentProfile = get(offenderidentifier);

        if (punishmentProfile == null) {
            return;
        }

        cache.remove(punishmentProfile.offender);
    }

    public static void clearCache() {
        cache.clear();
    }
    /* cache =========================================================================================== */

    private final Offender offender;
    private Map<Integer, PunishmentEntry> punishments;

    public PunishmentProfile(Offender offender) {
        this.offender = offender;
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

    public Callable<Void> show(final CommandSender caller) {
        return () -> {
            for (PunishmentType type : PunishmentType.values()) {
                final List<Punishment> punishments = getByType(type);
                boolean truncate = punishments.size() > 32;

                caller.sendMessage(Messages.COLOR_MAIN + WordUtils.capitalize(type.getPlural()) + " (" + punishments.size() + "):");

                if (truncate) {
                    caller.sendMessage(Messages.COLOR_WARN + offset + "Too many to show");
                    continue;
                }

                for (Punishment punishment : getByType(type)) {
                    StringJoiner joiner = new StringJoiner(" ");

                    joiner.add(Messages.COLOR_WARN + offset);
                    joiner.add(punishment.getInceptionTimeFormatted());

                    if (!punishment.getServerContext().equals("primary")) {
                        joiner.add("[" + punishment.getServerContext() + "]");
                    }

                    if (caller.hasPermission("ruinscraft.punishments.viewpunisher")) {
                        joiner.add("[" + PlayerLookups.getName(punishment.getPunisher()).call() + "]");
                    }

                    if (type.canBeTemporary()) {
                        joiner.add("[" + punishment.getTotalDurationWords() + "]");
                    }

                    joiner.add(":");
                    joiner.add(punishment.getReason());

                    caller.sendMessage(joiner.toString());
                }
            }
            return null;
        };
    }

}
