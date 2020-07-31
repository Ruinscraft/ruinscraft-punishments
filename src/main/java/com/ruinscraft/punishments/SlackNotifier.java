package com.ruinscraft.punishments;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.ChatColor;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class SlackNotifier {

    private static final Gson gson = new Gson();

    private final String webhookUrl;

    public SlackNotifier(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public CompletableFuture<Void> notify(PunishmentEntry entry) {
        String message = entry.creationMessage();
        message = ChatColor.stripColor(message);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("text", message);

        return CompletableFuture.supplyAsync(() -> {
            try {
                URL webookUrl = new URL(webhookUrl);
                HttpsURLConnection connection = (HttpsURLConnection) webookUrl.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-type", "application/json");
                connection.setDoOutput(true);
                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(gson.toJson(jsonObject));
                outputStream.flush();
                outputStream.close();
                connection.getResponseCode();
            } catch (Exception e) {
                PunishmentsPlugin.get().getLogger().warning("Could not post to slack channel. Check slack-webhook-url in the config.yml");
            }

            return null;
        });
    }

}
