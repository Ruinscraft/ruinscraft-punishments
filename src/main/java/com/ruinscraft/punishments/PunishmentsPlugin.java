package com.ruinscraft.punishments;

import org.bukkit.plugin.java.JavaPlugin;

public class PunishmentsPlugin extends JavaPlugin {

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
