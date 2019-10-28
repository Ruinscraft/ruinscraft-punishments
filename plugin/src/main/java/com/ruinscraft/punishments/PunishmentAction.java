package com.ruinscraft.punishments;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruinscraft.punishments.behaviors.PunishmentBehaviorRegistry;
import com.ruinscraft.punishments.messaging.Message;
import com.ruinscraft.punishments.offender.Offender;
import org.bukkit.ChatColor;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public enum PunishmentAction {
    CREATE, PARDON, DELETE;

    /***
     * Saves to the database, sends over the Messenger, notifies Slack
     * @param entry
     */
    public CompletableFuture<Void> performRemote(PunishmentEntry entry) {   // TODO: probably find a better name for this method
        return CompletableFuture.supplyAsync(() -> {
            /* Save to database */
            {
                PunishmentsPlugin.get().getStorage().callAction(entry, this);
            }
            /* Send over messenger */
            {
                Message message = new Message(entry, this);
                PunishmentsPlugin.get().getMessageManager().getDispatcher().dispatch(message);
            }
            /* Notify Slack webhook */
            {
                Gson gson = new Gson();
                try {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("text", ChatColor.stripColor(entry.creationMessage()));
                    URL webookUrl = new URL(PunishmentsPlugin.get().getConfig().getString("slack-webhook-url"));
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
            }
            return null;
        });
    }

    /***
     * Updates local cache and calls the punishment action
     * @param entry
     */
    public void performLocal(PunishmentEntry entry) {   // TODO: probably find a better name for this method
        Offender offender = entry.punishment.getOffender();
        Optional<PunishmentProfile> profile = PunishmentProfiles.getProfile(offender.getIdentifier());
        if (profile.isPresent()) {
            profile.get().update(entry, this);
        }
        PunishmentBehaviorRegistry.get(entry.type).perform(entry.punishment, this);
    }

}
