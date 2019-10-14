package com.ruinscraft.punishments.offender;

import com.ruinscraft.punishments.PunishmentsPlugin;
import com.ruinscraft.punishments.storage.Storage;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UUIDOffender extends Offender<UUID> {

    private static Storage storage = PunishmentsPlugin.get().getStorage();

    private transient Set<String> addresses;

    public UUIDOffender(UUID uuid) {
        super(uuid);
        this.addresses = new HashSet<>();
    }

    public CompletableFuture<Void> logAddress(String address) {
        return CompletableFuture.supplyAsync(() -> {
            storage.insertAddress(identifier, address);

            return null;
        });
    }

    public CompletableFuture<Void> loadAddresses() {
        return CompletableFuture.supplyAsync(() -> {
            addresses = PunishmentsPlugin.get().getStorage().queryAddresses(identifier).join();

            return null;
        });
    }

    public Set<String> getAddresses() {
        return addresses;
    }

}
