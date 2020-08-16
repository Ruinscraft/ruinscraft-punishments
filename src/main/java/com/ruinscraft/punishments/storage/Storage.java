package com.ruinscraft.punishments.storage;

import com.ruinscraft.punishments.AddressLog;
import com.ruinscraft.punishments.PunishmentAction;
import com.ruinscraft.punishments.PunishmentEntry;
import com.ruinscraft.punishments.offender.Offender;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Storage {

    // Punishments

    default CompletableFuture<Void> callAction(PunishmentEntry entry, PunishmentAction action) {
        switch (action) {
            case CREATE:
                return insert(entry);
            case PARDON:
                return update(entry);
            case DELETE:
                return delete(entry.punishment.getPunishmentId());
            default:
                throw new RuntimeException("Unknown data action");
        }
    }

    CompletableFuture<Void> insert(PunishmentEntry entry);

    CompletableFuture<Void> update(PunishmentEntry entry);

    CompletableFuture<Void> delete(int punishmentId);

    CompletableFuture<List<PunishmentEntry>> queryOffender(Offender offender);

    // Address logs

    CompletableFuture<List<AddressLog>> queryAddressLogs(UUID user);

    CompletableFuture<List<AddressLog>> queryAddressLogs(String address);

    CompletableFuture<Void> insertAddressLog(AddressLog addressLog);

    void close();

}
