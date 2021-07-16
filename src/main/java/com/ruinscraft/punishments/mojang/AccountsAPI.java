package com.ruinscraft.punishments.mojang;

import com.google.gson.Gson;
import com.ruinscraft.punishments.console;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public final class AccountsAPI {

    public static final AccountsProfile CONSOLE_PROFILE = new AccountsProfile(console.UUID.toString(), console.NAME);
    protected static final Gson GSON = new Gson();
    protected static final String URL_MATCH_NAME = "https://api.mojang.com/users/profiles/minecraft/%s";
    protected static final String URL_MATCH_ID = "https://api.mojang.com/user/profiles/%s/names";
    // uuid util, Mojang-style UUIDs don't have dashes
    private static final Pattern UUID_FIX = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

    public static CompletableFuture<AccountsProfile> getAccountsProfile(String name) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(String.format(URL_MATCH_NAME, name));
                try (InputStreamReader reader = new InputStreamReader(url.openStream())) {
                    return GSON.fromJson(reader, AccountsProfile.class);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        });
    }

    public static CompletableFuture<AccountsProfile> getAccountsProfile(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            if (uuid.equals(console.UUID)) {
                return CONSOLE_PROFILE;
            }

            final String id = uuid.toString().replace("-", "");

            try {
                URL url = new URL(String.format(URL_MATCH_ID, id));
                try (InputStreamReader reader = new InputStreamReader(url.openStream())) {
                    return GSON.fromJson(reader, AccountsProfile.class);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        });
    }

    private static UUID fixUUID(String uuidWithoutDashes) {
        return UUID.fromString(UUID_FIX.matcher(uuidWithoutDashes.replace("-", "")).replaceAll("$1-$2-$3-$4-$5"));
    }

    public static final class AccountsProfile {
        private final String id;
        private final String name;

        private AccountsProfile(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public UUID getUniqueId() {
            return fixUUID(id);
        }

        public String getName() {
            return name;
        }
    }

}
