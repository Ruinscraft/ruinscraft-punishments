package com.ruinscraft.punishments;

import com.ruinscraft.punishments.behaviors.PunishmentBehavior;
import com.ruinscraft.punishments.behaviors.PunishmentBehaviorRegistry;
import com.ruinscraft.punishments.storage.PunishmentStorage;

public class PunishmentEntry {

    public final Punishment punishment;
    public final PunishmentType type;

    private PunishmentEntry(Punishment punishment, PunishmentType type) {
        this.punishment = punishment;
        this.type = type;
    }

    public static PunishmentEntry of(Punishment punishment, PunishmentType type) {
        return new PunishmentEntry(punishment, type);
    }

    public void performAction(PunishmentAction action, boolean save) {
        PunishmentStorage storage = PunishmentsPlugin.get().getStorage();
        PunishmentBehavior behavior = PunishmentBehaviorRegistry.get(type);

        switch (action) {
            case CREATE:
                if (save) storage.insert(this);
                behavior.onCreate(this);
                break;
            case DELETE:
                if (save) storage.delete(punishment.getPunishmentId());
                behavior.onDelete(this);
                break;
            case PARDON:
                if (save) storage.update(this);
                behavior.onPardon(this);
                break;
        }
    }

}
