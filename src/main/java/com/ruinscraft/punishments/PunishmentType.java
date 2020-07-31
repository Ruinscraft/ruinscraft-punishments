package com.ruinscraft.punishments;

public enum PunishmentType {
    KICK("kicked", "kick", false),
    WARN("warned", "warn", false),
    MUTE("muted", "mute", true),
    BAN("banned", "ban", true);

    final String verb;
    final String noun;
    final boolean canBeTemporary;

    PunishmentType(String verb, String noun, boolean canBeTemporary) {
        this.verb = verb;
        this.noun = noun;
        this.canBeTemporary = canBeTemporary;
    }

    public static PunishmentType match(String label) {
        for (PunishmentType type : PunishmentType.values()) {
            if (label.toUpperCase().contains(type.name())) {
                return type;
            }
        }
        return null;
    }

    public String getVerb() {
        return verb;
    }

    public String getNoun() {
        return noun;
    }

    public String getPlural() {
        return getNoun() + "s";
    }

    public boolean canBeTemporary() {
        return canBeTemporary;
    }

}
