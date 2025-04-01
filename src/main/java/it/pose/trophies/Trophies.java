package it.pose.trophies;

import it.pose.trophies.commands.CommandsManager;
import it.pose.trophies.listeners.EventListener;
import it.pose.trophies.managers.ConfigManager;
import it.pose.trophies.managers.PlayerDataManager;
import it.pose.trophies.managers.TrophyManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Trophies extends JavaPlugin {

    private static Trophies instance;
    private ConfigManager configManager;
    private TrophyManager trophyManager;
    private PlayerDataManager playerDataManager;

    public String defaultTitle = "Trophies Showcase";

    @Override
    public void onEnable() {

        // "=^.^=" \\

        instance = this;

        saveResource("config.yml", false);
        saveResource("trophies.yml", false);

        configManager = new ConfigManager();
        trophyManager = new TrophyManager();
        playerDataManager = new PlayerDataManager();

        configManager.reloadConfig();
        trophyManager.reloadTrophies();


        getCommand("trophies").setExecutor(new CommandsManager());
        getServer().getPluginManager().registerEvents(new EventListener(), this);


        File playerDataFolder = new File(getDataFolder(), "playerData");
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }

        for (Player player : getServer().getOnlinePlayers()) {
            this.getLogger().info("Loading data for player " + player.getName());
            playerDataManager.loadPlayerData(player.getUniqueId());
        }


    }

    public static Trophies getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public TrophyManager getTrophyManager() {
        return trophyManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
}