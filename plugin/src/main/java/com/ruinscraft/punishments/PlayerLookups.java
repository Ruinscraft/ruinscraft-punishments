package com.ruinscraft.punishments;

import com.ruinscraft.punishments.mojang.AccountsAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

public final class PlayerLookups {

    private static final Map<String, UUID> name_uuid_cache = new HashMap<>();
    private static final Map<UUID, String> uuid_name_cache = new HashMap<>();

    public static Callable<String> getName(UUID uuid) {
        return () -> {
            if (uuid_name_cache.containsKey(uuid)) {
                return uuid_name_cache.get(uuid);
            }

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

            if (offlinePlayer.hasPlayedBefore()) {
                return offlinePlayer.getName();
            }

            AccountsAPI.AccountsProfile accountsProfile = AccountsAPI.getAccountsProfile(uuid);

            String name = null;

            if (accountsProfile != null) {
                name = accountsProfile.getName();
                uuid_name_cache.put(uuid, name);
                name_uuid_cache.put(name, uuid);
            }

            return name;
        };
    }

    public static Callable<UUID> getUniqueId(String name) {
        return () -> {
            if (name_uuid_cache.containsKey(name)) {
                return name_uuid_cache.get(name);
            }

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);

            if (offlinePlayer.hasPlayedBefore()) {
                return offlinePlayer.getUniqueId();
            }

            AccountsAPI.AccountsProfile accountsProfile = AccountsAPI.getAccountsProfile(name);

            UUID uuid = null;

            if (accountsProfile != null) {
                uuid = accountsProfile.getUniqueId();
                name_uuid_cache.put(accountsProfile.getName(), uuid);
                uuid_name_cache.put(uuid, accountsProfile.getName());
            }

            return uuid;
        };
    }

}
