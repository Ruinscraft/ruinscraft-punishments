package com.ruinscraft.punishments;

import com.ruinscraft.punishments.mojang.AccountsAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerLookups {

    private static final Map<String, UUID> name_uuid_cache = new ConcurrentHashMap<>();
    private static final Map<UUID, String> uuid_name_cache = new ConcurrentHashMap<>();

    public static CompletableFuture<String> getName(UUID uuid) {
        if (uuid_name_cache.containsKey(uuid)) {
            String name = uuid_name_cache.get(uuid);
            return CompletableFuture.completedFuture(name);
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        if (offlinePlayer.hasPlayedBefore()) {
            String name = offlinePlayer.getName();
            uuid_name_cache.put(uuid, name);
            name_uuid_cache.put(name, uuid);
            return CompletableFuture.completedFuture(name);
        }

        CompletableFuture<String> future = new CompletableFuture<>();

        AccountsAPI.getAccountsProfile(uuid).thenAccept(accountsProfile -> {
            if (accountsProfile == null) {
                future.complete(null);
                return;
            }

            String name = accountsProfile.getName();
            uuid_name_cache.put(uuid, name);
            name_uuid_cache.put(name, uuid);

            future.complete(name);
        });

        return future;
    }

    public static CompletableFuture<UUID> getUniqueId(String name) {
        if (name_uuid_cache.containsKey(name)) {
            UUID uuid = name_uuid_cache.get(name);
            return CompletableFuture.completedFuture(uuid);
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);

        if (offlinePlayer.hasPlayedBefore()) {
            UUID uuid = offlinePlayer.getUniqueId();
            name_uuid_cache.put(name, uuid);
            uuid_name_cache.put(uuid, name);
            return CompletableFuture.completedFuture(uuid);
        }

        CompletableFuture<UUID> future = new CompletableFuture<>();

        AccountsAPI.getAccountsProfile(name).thenAccept(accountsProfile -> {
            if (accountsProfile == null) {
                future.complete(null);
                return;
            }

            UUID uuid = accountsProfile.getUniqueId();
            String accountsProfileName = accountsProfile.getName(); // use this instead of #name to ensure proper capitalization
            name_uuid_cache.put(accountsProfileName, uuid);
            uuid_name_cache.put(uuid, accountsProfileName);

            future.complete(uuid);
        });

        return future;
    }

}
