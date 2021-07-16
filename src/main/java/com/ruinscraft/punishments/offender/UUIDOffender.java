package com.ruinscraft.punishments.offender;

import com.ruinscraft.punishments.AddressLog;
import com.ruinscraft.punishments.PunishmentsPlugin;
import com.ruinscraft.punishments.storage.PunishmentStorage;
import com.ruinscraft.punishments.util.Tasks;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UUIDOffender extends Offender<UUID> {

    private static final PunishmentStorage storage = PunishmentsPlugin.get().getStorage();

    private transient List<AddressLog> addressLogs;

    public UUIDOffender(UUID uuid) {
        super(uuid);
        addressLogs = new ArrayList<>();
    }

    @Override
    public boolean isOnline() {
        return Bukkit.getPlayer(identifier) != null;
    }

    @Override
    public void kick(String kickMsg) {
        Tasks.sync(() -> Bukkit.getPlayer(identifier).kickPlayer(kickMsg));
    }

    @Override
    public void sendMessage(String msg) {
        Tasks.sync(() -> Bukkit.getPlayer(identifier).sendMessage(msg));
    }

    public CompletableFuture<Void> loadAddressLogs() {
        return storage.queryAddressLogs(identifier).thenAccept(addressLogs -> UUIDOffender.this.addressLogs = addressLogs);
    }

    public CompletableFuture<Void> saveAddressLog(AddressLog addressLog) {
        addressLogs.add(addressLog);
        return storage.insertAddressLog(addressLog);
    }

    public List<AddressLog> getAddressLogs() {
        return addressLogs;
    }

}
