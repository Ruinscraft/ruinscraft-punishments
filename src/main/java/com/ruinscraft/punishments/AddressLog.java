package com.ruinscraft.punishments;

import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

public class AddressLog {

    private final UUID user;
    private final String address;
    private final String username;
    private final long usedAt;

    public AddressLog(UUID user, String address, String username, long usedAt) {
        this.user = user;
        this.address = address;
        this.username = username;
        this.usedAt = usedAt;
    }

    public UUID getUser() {
        return user;
    }

    public String getAddress() {
        return address;
    }

    public String getUsername() {
        return username;
    }

    public long getUsedAt() {
        return usedAt;
    }

    public static AddressLog of(AsyncPlayerPreLoginEvent event) {
        UUID user = event.getUniqueId();
        String address = event.getAddress().getHostAddress();
        String username = event.getName();
        long usedAt = System.currentTimeMillis();

        return new AddressLog(user, address, username, usedAt);
    }

}
