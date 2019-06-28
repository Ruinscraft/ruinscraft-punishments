package com.ruinscraft.punishments;

public enum PunishmentType {
    KICK("kicked", "kick", "kicks", false),
    WARN("warned", "warn", "warns", false),
    MUTE("muted", "mute", "mutes", true),
    BAN("banned", "ban", "bans", true);

    final String verb;
    final String noun;
    final String plural;
    final boolean canBeTemporary;

    PunishmentType(String verb, String noun, String plural, boolean canBeTemporary) {
        this.verb = verb;
        this.noun = noun;
        this.plural = plural;
        this.canBeTemporary = canBeTemporary;
    }

    public String getVerb() {
        return verb;
    }

    public String getNoun() {
        return noun;
    }

    public String getPlural() {
        return plural;
    }

    public boolean canBeTemporary() {
        return canBeTemporary;
    }

    public static PunishmentType match(String label) {
        for (PunishmentType type : PunishmentType.values()) {
            if (label.toUpperCase().contains(type.name())) {
                return type;
            }
        }
        return null;
    }

}
