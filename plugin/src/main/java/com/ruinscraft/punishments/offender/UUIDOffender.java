package com.ruinscraft.punishments.offender;

import com.ruinscraft.punishments.PunishmentsPlugin;
import com.ruinscraft.punishments.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UUIDOffender implements Offender<UUID> {

    private final UUID uuid;
    private Set<String> addresses;

    public UUIDOffender(UUID uuid) {
        this.uuid = uuid;
        this.addresses = new HashSet<>();
    }

    @Override
    public UUID getIdentifier() {
        return uuid;
    }

    @Override
    public boolean offerChatMessage(String msg) {
        Player offenderPlayer = Bukkit.getPlayer(uuid);

        if (offenderPlayer == null) {
            return false;
        }

        offenderPlayer.sendMessage(msg);

        return true;
    }

    @Override
    public boolean offerKick(String kickMsg) {
        Player offenderPlayer = Bukkit.getPlayer(uuid);

        if (offenderPlayer == null) {
            return false;
        }

        offenderPlayer.kickPlayer(kickMsg);

        return true;
    }

    public boolean registerAddress(String address) {
        if (getAddresses().add(address)) {
            Tasks.async(() -> {
                try {
                    PunishmentsPlugin.get().getStorage().insertAddress(uuid, address).call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            return true;
        } else {
            return false;
        }
    }

    public Set<String> getAddresses() {
        return addresses;
    }

}
