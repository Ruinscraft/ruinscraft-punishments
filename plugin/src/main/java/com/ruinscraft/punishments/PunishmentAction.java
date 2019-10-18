package com.ruinscraft.punishments;

import com.ruinscraft.punishments.behaviors.PunishmentBehaviorRegistry;
import com.ruinscraft.punishments.messaging.Message;
import com.ruinscraft.punishments.offender.Offender;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public enum PunishmentAction {
    CREATE, PARDON, DELETE;

    /***
     * Saves to the database and sends over the Messenger
     * @param entry
     */
    public CompletableFuture<Void> performRemote(PunishmentEntry entry) {
        return CompletableFuture.supplyAsync(() -> {
            PunishmentsPlugin.get().getStorage().callAction(entry, this);
            Message message = new Message(entry, this);
            PunishmentsPlugin.get().getMessageManager().getDispatcher().dispatch(message);
            return null;
        });
    }

    /***
     * Updates local cache and calls the punishment action
     * @param entry
     */
    public void performLocal(PunishmentEntry entry) {
        Offender offender = entry.punishment.getOffender();
        Optional<PunishmentProfile> profile = PunishmentProfiles.getProfile(offender.getIdentifier());

        if (profile.isPresent()) {
            profile.get().update(entry, this);
        }

        PunishmentBehaviorRegistry.get(entry.type).perform(entry.punishment, this);
    }

}
