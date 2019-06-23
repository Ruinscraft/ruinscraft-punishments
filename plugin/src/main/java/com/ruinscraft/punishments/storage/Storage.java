package com.ruinscraft.punishments.storage;

import com.ruinscraft.punishments.Punishment;
import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentEntry;
import com.ruinscraft.punishments.PunishmentType;

import java.util.List;
import java.util.concurrent.Callable;

public interface Storage {

    default void callAction(PunishmentEntry entry, PunishmentAction action) {
        switch (action) {
            case CREATE:
                insert(entry);
                return;
            case UNDO:
            case DELETE:
                delete(entry.punishment.getPunishmentId());
                return;
        }
    }

    Callable<Void> insert(PunishmentEntry entry);

    Callable<Void> delete(int punishmentId);

    Callable<List<PunishmentEntry>> query(String offender);

    Callable<List<Punishment>> queryByType(String offender, PunishmentType type);

}
