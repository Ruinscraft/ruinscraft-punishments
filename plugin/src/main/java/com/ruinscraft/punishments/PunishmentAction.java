package com.ruinscraft.punishments;

public enum PunishmentAction {
    CREATE, UNDO, DELETE;

    public void sync(PunishmentEntry entry) {
        TransientPunisherHistory.insert(entry);
    }

}
