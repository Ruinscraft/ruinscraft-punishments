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

}
