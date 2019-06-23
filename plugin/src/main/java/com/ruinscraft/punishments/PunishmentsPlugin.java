package com.ruinscraft.punishments;

import com.ruinscraft.punishments.commands.*;
import com.ruinscraft.punishments.messaging.MessageManager;
import com.ruinscraft.punishments.messaging.redis.RedisMessageManager;
import com.ruinscraft.punishments.storage.MySQLStorage;
import com.ruinscraft.punishments.storage.Storage;
import org.bukkit.plugin.java.JavaPlugin;

public class PunishmentsPlugin extends JavaPlugin {

    private Storage storage;
    private MessageManager messageManager;

    public Storage getStorage() {
        return storage;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    @Override
    public void onEnable() {
        singleton = this;

        saveDefaultConfig();

        if (getServer().getPluginManager().getPlugin("BanManager") != null) {
            getLogger().warning("BanManager is loaded on this server. Please remove it.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // setup storage
        String mysqlHost = getConfig().getString("storage.mysql.host");
        int mysqlPort = getConfig().getInt("storage.mysql.port");
        String mysqlDatabase = getConfig().getString("storage.mysql.database");
        String mysqlUsername = getConfig().getString("storage.mysql.username");
        String mysqlPassword = getConfig().getString("storage.mysql.password");
        storage = new MySQLStorage(mysqlHost, mysqlPort, mysqlDatabase, mysqlUsername, mysqlPassword.toCharArray());

        // setup message manager
        String redisHost = getConfig().getString("messaging.redis.host");
        int redisPort = getConfig().getInt("messaging.redis.port");
        messageManager = new RedisMessageManager(redisHost, redisPort);

        // new punishment commands
        NewPunishmentCommand newPunishmentCommand = new NewPunishmentCommand();
        getCommand("warn").setExecutor(newPunishmentCommand);
        getCommand("mute").setExecutor(newPunishmentCommand);
        getCommand("tempmute").setExecutor(newPunishmentCommand);
        getCommand("ban").setExecutor(newPunishmentCommand);
        getCommand("tempban").setExecutor(newPunishmentCommand);

        // pardon punishment commands (unmute, unban)
        PardonPunishmentCommand pardonPunishmentCommand = new PardonPunishmentCommand();
        getCommand("unmute").setExecutor(pardonPunishmentCommand);
        getCommand("unban").setExecutor(pardonPunishmentCommand);

        // undo punishment command
        UndoPunishmentCommand undoPunishmentCommand = new UndoPunishmentCommand();
        getCommand("pundo").setExecutor(undoPunishmentCommand);

        // delete punishment command (by id, removes from the records)
        DeletePunishmentCommand deletePunishmentCommand = new DeletePunishmentCommand();
        getCommand("pdel").setExecutor(deletePunishmentCommand);

        // query punishment commands
        QueryPunishmentCommand queryPunishmentCommand = new QueryPunishmentCommand();
        getCommand("pinfo").setExecutor(queryPunishmentCommand);
    }

    @Override
    public void onDisable() {
        if (storage != null) {
            storage.close();
        }

        if (messageManager != null) {
            messageManager.close();
        }

        singleton = null;
    }

    private static PunishmentsPlugin singleton;

    public static PunishmentsPlugin get() {
        return singleton;
    }

}
