package com.ruinscraft.punishments;

import com.ruinscraft.punishments.offender.Offender;
import com.ruinscraft.punishments.util.Messages;
import org.apache.commons.lang.WordUtils;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PunishmentProfile {

    protected final Offender offender;
    protected final Map<Integer, PunishmentEntry> punishments;
    protected final Set<PunishmentProfile> related;

    public PunishmentProfile(Offender offender) {
        this.offender = offender;
        punishments = new HashMap<>();
        related = new HashSet<>();
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
                .filter(p -> p.isThisServer())
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

    public boolean wasRecentlyPunished() {
        PunishmentEntry mostRecent = getMostRecent();

        if (mostRecent != null) {
            long timeDiff = System.currentTimeMillis() - mostRecent.punishment.getInceptionTime();
            if (timeDiff < TimeUnit.SECONDS.toMillis(10)) {
                return true;
            }
        }

        return false;
    }

    public boolean isAlready(PunishmentType type) {
        if (type.canBeTemporary() && (getActive(type) != null)) {
            return true;
        }

        return false;
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

    public void addRelated(PunishmentProfile profile) {
        if (profile == this) {
            return;
        }

        related.add(profile);
    }

    public void removeRelated(PunishmentProfile profile) {
        related.remove(profile);
    }

    public boolean isRelated(PunishmentProfile profile) {
        return related.contains(profile);
    }

    public Set<PunishmentProfile> getRelated() {
        return related;
    }

    public PunishmentProfile getEvading(PunishmentType type) {
        for (PunishmentProfile related : related) {
            if (related.isAlready(type)) {
                return related;
            }
        }

        return null;
    }

    public boolean isEvading(PunishmentType type) {
        return getEvading(type) != null;
    }

    private static final String SHOW_OFFSET = " ";

    public void show(CommandSender caller, PunishmentType type) {
        List<Punishment> toShow = getByType(type);
        Map<String, ArrayList<Punishment>> toShowByDate = new HashMap<>();
        boolean showMoreInfo = caller.hasPermission("ruinscraft.punishments.moreinfo");

        for (Punishment punishment : toShow) {
            String date = punishment.getInceptionTimeFormatted();

            if (!toShowByDate.containsKey(date)) {
                toShowByDate.put(date, new ArrayList<>());
            }

            toShowByDate.get(date).add(punishment);
        }

        caller.sendMessage(Messages.COLOR_MAIN + type.getPluralCapitalized() + " (" + toShow.size() + "):");

        for (String date : toShowByDate.keySet()) {
            List<Punishment> punishments = toShowByDate.get(date);

            caller.sendMessage(Messages.COLOR_MAIN + SHOW_OFFSET + "== " + date + " ==");

            for (Punishment punishment : punishments) {
                StringJoiner joiner = new StringJoiner(" ");

                joiner.add(Messages.COLOR_WARN + SHOW_OFFSET);
                joiner.add(WordUtils.capitalize(type.getVerb()) + " for " + punishment.getReason());
                joiner.add("while on " + punishment.getServer());

                if (type.canBeTemporary()) {
                    joiner.add("for " + punishment.getTotalDurationWords());
                }

//                if (showMoreInfo) {
//                    joiner.add(String.format("[id=%d,by=%s]", punishment.getPunishmentId(), punishment.getPunisherUsername()) + Messages.COLOR_WARN);
//                }

                caller.sendMessage(joiner.toString());
            }
        }
    }

    public void showAll(CommandSender caller) {
        for (PunishmentType type : PunishmentType.values()) {
            show(caller, type);
        }
    }

}
