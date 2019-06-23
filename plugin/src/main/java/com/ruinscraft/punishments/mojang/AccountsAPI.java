package com.ruinscraft.punishments.mojang;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Pattern;

public final class AccountsAPI {

    protected static final Gson GSON = new Gson();
    protected static final String URL_MATCH_NAME = "https://api.mojang.com/users/profiles/minecraft/%s";
    protected static final String URL_MATCH_ID = "https://api.mojang.com/user/profiles/%s/names";

    public static AccountsProfile getAccountsProfile(String name) throws IOException {
        URL url = new URL(String.format(URL_MATCH_NAME, name));
        try (InputStreamReader reader = new InputStreamReader(url.openStream())) {
            return GSON.fromJson(reader, AccountsProfile.class);
        }
    }

    public static AccountsProfile getAccountsProfile(UUID uuid) throws IOException {
        final String id = uuid.toString().replace("-", "");
        URL url = new URL(String.format(URL_MATCH_ID, id));
        try (InputStreamReader reader = new InputStreamReader(url.openStream())) {
            return GSON.fromJson(reader, AccountsProfile.class);
        }
    }

    public static final class AccountsProfile {
        protected String id;
        protected String name;

        private AccountsProfile() {
        }

        public UUID getUniqueId() {
            return formatFromInput(id);
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "[id=" + id + ", name=" + name + "]";
        }
    }

    // uuid util
    private static final Pattern UUID_FIX = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

    private static UUID formatFromInput(String uuid) {
        return UUID.fromString(UUID_FIX.matcher(uuid.replace("-", "")).replaceAll("$1-$2-$3-$4-$5"));
    }

}
