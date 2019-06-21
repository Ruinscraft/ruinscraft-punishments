package com.ruinscraft.punishments.dispatcher;

import com.ruinscraft.punishments.Punishment;
import com.ruinscraft.punishments.PunishmentEntry;
import com.ruinscraft.punishments.PunishmentType;
import com.ruinscraft.punishments.PunishmentsPlugin;

public class WarnHandler extends PunishmentHandler {

    private PunishmentsPlugin plugin;
    private final PunishmentType TYPE = PunishmentType.WARN;

    @Override
    public void handle(Punishment punishment) {
        try {
            plugin.getStorage().insert(PunishmentEntry.of(punishment, TYPE)).call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
