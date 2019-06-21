package com.ruinscraft.punishments.dispatcher;

import com.ruinscraft.punishments.Punishment;
import com.ruinscraft.punishments.PunishmentsPlugin;

public class WarnHandler extends PunishmentHandler {

    private PunishmentsPlugin plugin;

    @Override
    public void handle(Punishment punishment) {
        try {
            plugin.getStorage().insert(punishment).call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
