package com.ruinscraft.punishments.storage;

import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentEntry;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

public interface Storage {

    default void callAction(PunishmentEntry entry, PunishmentAction action) throws Exception {
        switch (action) {
            case CREATE:
                insert(entry).call();
                break;
            case PARDON:
                update(entry).call();
                break;
            case DELETE:
                delete(entry.punishment.getPunishmentId()).call();
                break;
        }
    }

    Callable<Void> insert(PunishmentEntry entry);

    Callable<Void> update(PunishmentEntry entry);

    Callable<Void> delete(int punishmentId);

    Callable<List<PunishmentEntry>> query(UUID offender);

    default void close() {
    }

}
