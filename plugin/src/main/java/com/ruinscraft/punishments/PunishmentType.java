package com.ruinscraft.punishments;

public enum PunishmentType {
    KICK("kicked"),
    WARN("warned"),
    MUTE("muted"),
    BAN("banned");

    final String verb;

    PunishmentType(String verb) {
        this.verb = verb;
    }

    public String getVerb() {
        return verb;
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
