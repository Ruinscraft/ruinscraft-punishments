package com.ruinscraft.punishments;

import com.ruinscraft.punishments.offender.Offender;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PunishmentProfile {

    private static final String SHOW_OFFSET = " ";
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
                .filter(Punishment::isThisServer)
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

    public boolean wasRecentlyPunished() {
        PunishmentEntry mostRecent = getMostRecent();

        if (mostRecent != null) {
            long timeDiff = System.currentTimeMillis() - mostRecent.punishment.getInceptionTime();
            return timeDiff < TimeUnit.SECONDS.toMillis(5);
        }

        return false;
    }

    public boolean isAlready(PunishmentType type) {
        return type.canBeTemporary() && (getActive(type) != null);
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

    public void show(final CommandSender caller, final PunishmentType type) {
        boolean showStaffInfo = caller.hasPermission("group.helper");
        final List<Punishment> toShow = this.getByType(type);
        final List<String> punishmentLines = new ArrayList<>();
        for (final Punishment punishment : toShow) {
            final StringJoiner joiner = new StringJoiner(" ");
            joiner.add(ChatColor.YELLOW + punishment.getInceptionTimeFormatted());
            joiner.add(ChatColor.RED + "(" + punishment.getServer() + ")");
            joiner.add(punishment.getReason());
            if (punishment.isTemporary())
                joiner.add(ChatColor.GRAY + punishment.getTotalDurationWords());
            if (showStaffInfo)
                joiner.add(ChatColor.YELLOW + "[STAFF::id=" + punishment.getPunishmentId() + ",by=" + punishment.getPunisherUsername() + "]");
            punishmentLines.add(joiner.toString());
        }
        caller.sendMessage(ChatColor.GOLD + type.getPluralCapitalized() + " (" + punishmentLines.size() + ")");
        for (final String punishmentLine : punishmentLines) {
            caller.sendMessage(" " + punishmentLine);
        }
    }

    public void showAll(final CommandSender caller) {
        for (final PunishmentType type : PunishmentType.values()) {
            this.show(caller, type);
        }
    }

}
