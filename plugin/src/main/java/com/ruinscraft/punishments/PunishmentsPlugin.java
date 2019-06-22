package com.ruinscraft.punishments;

import com.ruinscraft.punishments.commands.DeletePunishmentCommand;
import com.ruinscraft.punishments.commands.NewPunishmentCommand;
import com.ruinscraft.punishments.commands.QueryPunishmentCommand;
import com.ruinscraft.punishments.commands.UndoPunishmentCommand;
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

        if (getServer().getPluginManager().getPlugin("BanManager") != null) {
            getLogger().warning("BanManager is loaded on this server. Please remove it.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // new punishment commands
        NewPunishmentCommand newPunishmentCommand = new NewPunishmentCommand();
        getCommand("warn").setExecutor(newPunishmentCommand);
        getCommand("mute").setExecutor(newPunishmentCommand);
        getCommand("tempmute").setExecutor(newPunishmentCommand);
        getCommand("ban").setExecutor(newPunishmentCommand);
        getCommand("tempban").setExecutor(newPunishmentCommand);

        // undo punishment commands (unmute, unban)
        UndoPunishmentCommand undoPunishmentCommand = new UndoPunishmentCommand();
        getCommand("unmute").setExecutor(undoPunishmentCommand);
        getCommand("unban").setExecutor(undoPunishmentCommand);

        // delete punishment commands (by id, removes from the records)
        DeletePunishmentCommand deletePunishmentCommand = new DeletePunishmentCommand();
        getCommand("delwarn").setExecutor(deletePunishmentCommand);
        getCommand("delmute").setExecutor(deletePunishmentCommand);
        getCommand("delban").setExecutor(deletePunishmentCommand);

        // query punishment commands
        QueryPunishmentCommand queryPunishmentCommand = new QueryPunishmentCommand();
        getCommand("pinfo").setExecutor(queryPunishmentCommand);
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
