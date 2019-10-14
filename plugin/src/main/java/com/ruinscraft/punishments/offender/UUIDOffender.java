package com.ruinscraft.punishments.offender;

import com.ruinscraft.punishments.PunishmentsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;

public class UUIDOffender extends Offender<UUID> {

    private transient Set<String> addresses;

    public UUIDOffender(UUID uuid) {
        super(uuid);
        this.addresses = new HashSet<>();
    }

    @Override
    public boolean offerChatMessage(String msg) {
        Player offenderPlayer = Bukkit.getPlayer(identifier);

        if (offenderPlayer == null) {
            return false;
        }

        offenderPlayer.sendMessage(msg);

        return true;
    }

    @Override
    public boolean offerKick(String kickMsg) {
        Player offenderPlayer = Bukkit.getPlayer(identifier);

        if (offenderPlayer == null) {
            return false;
        }

        offenderPlayer.kickPlayer(kickMsg);

        return true;
    }

    public Callable<Boolean> registerAddress(String address) {
        return () -> {
            if (addresses.add(address)) {
                try {
                    PunishmentsPlugin.get().getStorage().insertAddress(identifier, address).call();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            } else {
                return false;
            }
        };
    }

    public Callable<Void> loadAddresses() {
        return () -> {
            addresses = PunishmentsPlugin.get().getStorage().getAddresses(identifier).call();

            return null;
        };
    }

    public Set<String> getAddresses() {
        return addresses;
    }

}
