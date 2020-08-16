package com.ruinscraft.punishments.offender;

import com.ruinscraft.punishments.AddressLog;
import com.ruinscraft.punishments.PlayerLookups;
import com.ruinscraft.punishments.PunishmentsPlugin;
import com.ruinscraft.punishments.storage.Storage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UUIDOffender extends Offender<UUID> {

    private static final Storage storage = PunishmentsPlugin.get().getStorage();

    private transient List<AddressLog> addressLogs;

    public UUIDOffender(UUID uuid) {
        super(uuid);
        addressLogs = new ArrayList<>();
    }

    public CompletableFuture<Void> loadAddressLogs() {
        return storage.queryAddressLogs(identifier)
                .thenAccept(addressLogs -> UUIDOffender.this.addressLogs = addressLogs);
    }

    public CompletableFuture<Void> saveAddressLog(AddressLog addressLog) {
        addressLogs.add(addressLog);

        return storage.insertAddressLog(addressLog);
    }

    public List<AddressLog> getAddressLogs() {
        return addressLogs;
    }

    public CompletableFuture<String> getUsername() {
        return PlayerLookups.getName(identifier);
    }

}
