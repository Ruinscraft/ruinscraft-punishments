package com.ruinscraft.punishments;

import com.ruinscraft.punishments.offender.Offender;
import com.ruinscraft.punishments.util.Messages;
import org.apache.commons.lang.WordUtils;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PunishmentProfile {

    protected final Offender offender;
    protected final Map<Integer, PunishmentEntry> punishments;

    public PunishmentProfile(Offender offender) {
        this.offender = offender;
        this.punishments = new HashMap<>();
    }

    public Offender getOffender() {
        return offender;
    }

    public boolean hasPunishments() {
        return !punishments.isEmpty();
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

    // TODO: work on this formatting
    public CompletableFuture<Void> show(final CommandSender caller) {
        return CompletableFuture.supplyAsync(() -> {
            final String OFFSET = "    ";

            for (PunishmentType type : PunishmentType.values()) {
                final List<Punishment> punishments = getByType(type);
                boolean truncate = punishments.size() > 32;

                caller.sendMessage(Messages.COLOR_MAIN + WordUtils.capitalize(type.getPlural()) + " (" + punishments.size() + "):");

                if (truncate) {
                    caller.sendMessage(Messages.COLOR_WARN + OFFSET + "Too many to show");
                    continue;
                }

                for (Punishment punishment : getByType(type)) {
                    StringJoiner joiner = new StringJoiner(" ");

                    joiner.add(Messages.COLOR_WARN + OFFSET);
                    joiner.add(punishment.getInceptionTimeFormatted());

                    if (!punishment.getServerContext().equals("primary")) {
                        joiner.add("[" + punishment.getServerContext() + "]");
                    }

                    if (caller.hasPermission("ruinscraft.punishments.viewpunisher")) {
                        joiner.add("[" + PlayerLookups.getName(punishment.getPunisher()).join() + "]");
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
        });
    }

}
