package com.ruinscraft.punishments;

import com.ruinscraft.punishments.storage.PunishmentStorage;
import org.bukkit.plugin.java.JavaPlugin;

public class PunishmentsPlugin extends JavaPlugin {

    private PunishmentStorage storage;

    public PunishmentStorage getStorage() {
        return storage;
    }

    @Override
    public void onEnable() {
        singleton = this;
    }

    @Override
    public void onDisable() {
        singleton = null;
    }

    private static PunishmentsPlugin singleton;

    public static PunishmentsPlugin get() {
        return singleton;
    }

}
