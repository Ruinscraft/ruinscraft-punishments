package com.ruinscraft.punishments;

import com.ruinscraft.punishments.commands.*;
import com.ruinscraft.punishments.messaging.MessageManager;
import com.ruinscraft.punishments.messaging.redis.RedisMessageManager;
import com.ruinscraft.punishments.offender.OnlineUUIDOffender;
import com.ruinscraft.punishments.storage.PooledMySQLStorage;
import com.ruinscraft.punishments.storage.Storage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PunishmentsPlugin extends JavaPlugin {

    private Storage storage;
    private MessageManager messageManager;
    private SlackNotifier slackNotifier;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        setupMessaging();
        setupStorage();
        setupCommands();
        setupSlackNotifier();

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        Bukkit.getOnlinePlayers().forEach(player -> PunishmentProfiles.getOrLoadProfile(player.getUniqueId(), OnlineUUIDOffender.class));
    }

    @Override
    public void onDisable() {
        PunishmentProfiles.clear();

        if (storage != null) {
            storage.close();
        }

        if (messageManager != null) {
            messageManager.close();
        }

        instance = null;
    }

    private void setupMessaging() {
        String redisHost = getConfig().getString("messaging.redis.host");
        int redisPort = getConfig().getInt("messaging.redis.port");
        messageManager = new RedisMessageManager(redisHost, redisPort);
    }

    private void setupStorage() {
        String mysqlHost = getConfig().getString("storage.mysql.host");
        int mysqlPort = getConfig().getInt("storage.mysql.port");
        String mysqlDatabase = getConfig().getString("storage.mysql.database");
        String mysqlUsername = getConfig().getString("storage.mysql.username");
        String mysqlPassword = getConfig().getString("storage.mysql.password");
        storage = new PooledMySQLStorage(mysqlHost, mysqlPort, mysqlDatabase, mysqlUsername, mysqlPassword.toCharArray());
    }

    private void setupCommands() {
        NewPunishmentCommand newPunishmentCommand = new NewPunishmentCommand();
        getCommand("kick").setExecutor(newPunishmentCommand);
        getCommand("kickip").setExecutor(newPunishmentCommand);
        getCommand("warn").setExecutor(newPunishmentCommand);
        getCommand("warnip").setExecutor(newPunishmentCommand);
        getCommand("mute").setExecutor(newPunishmentCommand);
        getCommand("muteip").setExecutor(newPunishmentCommand);
        getCommand("tempmute").setExecutor(newPunishmentCommand);
        getCommand("tempmuteip").setExecutor(newPunishmentCommand);
        getCommand("ban").setExecutor(newPunishmentCommand);
        getCommand("banip").setExecutor(newPunishmentCommand);
        getCommand("tempban").setExecutor(newPunishmentCommand);
        getCommand("tempbanip").setExecutor(newPunishmentCommand);

        PardonPunishmentCommand pardonPunishmentCommand = new PardonPunishmentCommand();
        getCommand("unmute").setExecutor(pardonPunishmentCommand);
        getCommand("unmuteip").setExecutor(pardonPunishmentCommand);
        getCommand("unban").setExecutor(pardonPunishmentCommand);
        getCommand("unbanip").setExecutor(pardonPunishmentCommand);

        UndoPunishmentCommand undoPunishmentCommand = new UndoPunishmentCommand();
        getCommand("pundo").setExecutor(undoPunishmentCommand);

        DeletePunishmentCommand deletePunishmentCommand = new DeletePunishmentCommand();
        getCommand("pdel").setExecutor(deletePunishmentCommand);

        QueryPunishmentCommand queryPunishmentCommand = new QueryPunishmentCommand();
        getCommand("pinfo").setExecutor(queryPunishmentCommand);
        getCommand("pinfoip").setExecutor(queryPunishmentCommand);

        AddressInfoCommandExecutor addressInfoCommandExecutor = new AddressInfoCommandExecutor();
        getCommand("addrinfo").setExecutor(addressInfoCommandExecutor);
    }

    private void setupSlackNotifier() {
        String webhookUrl = getConfig().getString("slack-webhook-url");

        if (webhookUrl == null || webhookUrl.equals("notset")) {
            getLogger().warning("slack-webhook-url was not set in the config");
            return;
        }

        slackNotifier = new SlackNotifier(webhookUrl);
    }

    public Storage getStorage() {
        return storage;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public SlackNotifier getSlackNotifier() {
        return slackNotifier;
    }

    public String getServerName() {
        return getConfig().getString("server");
    }

    private static PunishmentsPlugin instance;

    public static PunishmentsPlugin get() {
        return instance;
    }

}
