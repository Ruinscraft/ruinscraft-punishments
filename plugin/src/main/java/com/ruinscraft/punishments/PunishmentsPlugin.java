package com.ruinscraft.punishments;

import com.ruinscraft.punishments.commands.*;
import com.ruinscraft.punishments.messaging.MessageManager;
import com.ruinscraft.punishments.messaging.redis.RedisMessageManager;
import com.ruinscraft.punishments.offender.UUIDOffender;
import com.ruinscraft.punishments.storage.MySQLStorage;
import com.ruinscraft.punishments.storage.Storage;
import com.ruinscraft.punishments.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
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

        PluginCommand kickCommand = getCommand("kick");
        kickCommand.setExecutor(newPunishmentCommand);

        PluginCommand warnCommand = getCommand("warn");
        warnCommand.setExecutor(newPunishmentCommand);

        PluginCommand muteCommand = getCommand("mute");
        muteCommand.setExecutor(newPunishmentCommand);

        PluginCommand tempMuteCommand = getCommand("tempmute");
        tempMuteCommand.setExecutor(newPunishmentCommand);

        PluginCommand banCommand = getCommand("ban");
        banCommand.setExecutor(newPunishmentCommand);

        PluginCommand tempBanCommand = getCommand("tempban");
        tempBanCommand.setExecutor(newPunishmentCommand);

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

        // register listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        Tasks.async(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                UUIDOffender uuidOffender = new UUIDOffender(player.getUniqueId());

                try {
                    PunishmentProfile.load(uuidOffender).call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onDisable() {
        PunishmentProfile.clearCache();

        if (storage != null) {
            storage.close();
        }

        if (messageManager != null) {
            messageManager.close();
        }

        singleton = null;
    }

    public static String getServerContext() {
        return get().getConfig().getString("server-context");
    }

    private static PunishmentsPlugin singleton;

    public static PunishmentsPlugin get() {
        return singleton;
    }

}
