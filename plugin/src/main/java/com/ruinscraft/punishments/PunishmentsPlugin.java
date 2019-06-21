package com.ruinscraft.punishments;

import com.ruinscraft.punishments.commands.PunishmentCommand;
import com.ruinscraft.punishments.storage.Storage;
import org.bukkit.plugin.java.JavaPlugin;

public class PunishmentsPlugin extends JavaPlugin {

    private Storage storage;

    public Storage getStorage() {
        return storage;
    }

    @Override
    public void onEnable() {
        singleton = this;

        PunishmentCommand commandExecutor = new PunishmentCommand();

        getCommand("warn").setExecutor(commandExecutor);
        getCommand("mute").setExecutor(commandExecutor);
        getCommand("tempmute").setExecutor(commandExecutor);
        getCommand("ban").setExecutor(commandExecutor);
        getCommand("tempban").setExecutor(commandExecutor);
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
