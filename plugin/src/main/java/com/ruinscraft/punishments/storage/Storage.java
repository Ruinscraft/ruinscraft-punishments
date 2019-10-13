package com.ruinscraft.punishments.storage;

import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentEntry;
import com.ruinscraft.punishments.offender.Offender;

import java.util.List;
import java.util.Set;
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

    // Punishments

    Callable<Void> insert(PunishmentEntry entry);

    Callable<Void> update(PunishmentEntry entry);

    Callable<Void> delete(int punishmentId);

    Callable<List<PunishmentEntry>> queryOffender(Offender offender);

    Callable<List<PunishmentEntry>> queryPunisher(UUID punisher);

    // IP logging

    Callable<Set<String>> getAddresses(UUID user);

    Callable<Void> insertAddress(UUID user, String address);

    Callable<Set<UUID>> getUsersForAddress(String address);

    default void close() {
    }

}
