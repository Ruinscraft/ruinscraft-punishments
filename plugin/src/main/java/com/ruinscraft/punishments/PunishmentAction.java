package com.ruinscraft.punishments;

import com.ruinscraft.punishments.behaviors.PunishmentBehaviorRegistry;
import com.ruinscraft.punishments.messaging.Message;
import com.ruinscraft.punishments.offender.Offender;

public enum PunishmentAction {
    CREATE, PARDON, DELETE;

    /***
     * Saves to the database, sends over the Messenger, notifies Slack
     * Should only get called on the server that initiates the Action
     * @param entry
     */
    public void performRemote(PunishmentEntry entry) {
        final Message message = new Message(entry, this);
        PunishmentsPlugin.get().getStorage().callAction(entry, this);
        PunishmentsPlugin.get().getMessageManager().getDispatcher().dispatch(message);
        PunishmentsPlugin.get().getSlackNotifier().notify(entry);
    }

    /***
     * Updates local cache and calls the punishment action
     * Should get called on all servers
     * @param entry
     */
    public void performLocal(PunishmentEntry entry) {
        Offender offender = entry.punishment.getOffender();
        PunishmentProfiles.getProfile(offender.getIdentifier()).ifPresent(profile -> profile.update(entry, this));
        PunishmentBehaviorRegistry.get(entry.type).perform(entry.punishment, this);
    }

}
