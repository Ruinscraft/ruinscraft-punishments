package com.ruinscraft.punishments.storage;

import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentEntry;
import com.ruinscraft.punishments.offender.Offender;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Storage {

    default CompletableFuture<Void> callAction(PunishmentEntry entry, PunishmentAction action) {
        return CompletableFuture.supplyAsync(() -> {
            switch (action) {
                case CREATE:
                    insert(entry);
                    break;
                case PARDON:
                    update(entry);
                    break;
                case DELETE:
                    delete(entry.punishment.getPunishmentId());
                    break;
            }

            return null;
        });
    }

    // Punishments

    CompletableFuture<Void> insert(PunishmentEntry entry);

    CompletableFuture<Void> update(PunishmentEntry entry);

    CompletableFuture<Void> delete(int punishmentId);

    CompletableFuture<List<PunishmentEntry>> queryOffender(Offender offender);

    CompletableFuture<List<PunishmentEntry>> queryPunisher(UUID punisher);

    // IP logging

    CompletableFuture<Set<String>> queryAddresses(UUID user);

    CompletableFuture<Void> insertAddress(UUID user, String address);

    CompletableFuture<Set<UUID>> queryUsersOnAddress(String address);

    default void close() {
    }

}
