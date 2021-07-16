package com.ruinscraft.punishments;

import com.ruinscraft.punishments.commands.*;
import com.ruinscraft.punishments.offender.IPOffender;
import com.ruinscraft.punishments.offender.UUIDOffender;
import com.ruinscraft.punishments.storage.PooledMySQLPunishmentStorage;
import com.ruinscraft.punishments.storage.PunishmentStorage;
import org.bukkit.plugin.java.JavaPlugin;

public class PunishmentsPlugin extends JavaPlugin {

    private static PunishmentsPlugin instance;
    private PunishmentStorage storage;
    private SlackNotifier slackNotifier;

    public static PunishmentsPlugin get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        setupStorage();
        setupCommands();
        setupSlackNotifier();

        if (storage == null) {
            getLogger().warning("No storage defined");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        // In case of reload
        getServer().getOnlinePlayers().forEach(player -> {
            UUIDOffender uuidOffender = new UUIDOffender(player.getUniqueId());
            IPOffender ipOffender = new IPOffender(player.getAddress().getHostString());

            PunishmentProfiles.getOrLoadProfile(uuidOffender).join();
            PunishmentProfiles.getOrLoadProfile(ipOffender).join();
        });
    }

    @Override
    public void onDisable() {
        if (storage != null){
            storage.close();
        }
    }

    private void setupStorage() {
        String host = getConfig().getString("storage.mysql.host");
        int port = getConfig().getInt("storage.mysql.port");
        String db = getConfig().getString("storage.mysql.database");
        String user = getConfig().getString("storage.mysql.username");
        String pass = getConfig().getString("storage.mysql.password");
        storage = new PooledMySQLPunishmentStorage(host, port, db, user, pass);
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

    public PunishmentStorage getStorage() {
        return storage;
    }

    public SlackNotifier getSlackNotifier() {
        return slackNotifier;
    }

    public String getServerName() {
        return getConfig().getString("server");
    }

}
