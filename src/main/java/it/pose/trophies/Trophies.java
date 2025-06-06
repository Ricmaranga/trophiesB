package it.pose.trophies;

import it.pose.trophies.commands.CommandsManager;
import it.pose.trophies.listeners.EventListener;
import it.pose.trophies.managers.ConfigManager;
import it.pose.trophies.managers.PlayerDataManager;
import it.pose.trophies.managers.TrophyManager;
import it.pose.trophies.trophies.Trophy;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Trophies extends JavaPlugin {

    private static Trophies instance;
    private TrophyManager trophyManager;
    private PlayerDataManager playerDataManager;

    public static Map<UUID, Trophy> trophies = new HashMap<>();

    @Override
    public void onEnable() {

        // "=^.^=" \\

        instance = this;

        saveResource("config.yml", false);
        saveResource("trophies.yml", false);

        ConfigManager configManager = new ConfigManager();
        trophyManager = new TrophyManager();
        playerDataManager = new PlayerDataManager();

        PlayerDataManager.loadAll();

        configManager.reloadConfig();
        trophyManager.reloadTrophies();

        ConfigManager.init(this);
        TrophyManager.loadTrophies();

        trophies = TrophyManager.getAllTrophies();

        getCommand("trophies").setExecutor(new CommandsManager());
        getServer().getPluginManager().registerEvents(new EventListener(), this);


        File playerDataFolder = new File(getDataFolder(), "playerData");
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }

        saveDefaultConfig();
        Lang.init(this);

        new Metrics(this, 25989);
        new UpdateChecker(this, 125457).checkForUpdate();
    }

    @Override
    public void onDisable() {
        PlayerDataManager.saveAll();
    }

    public static Trophies getInstance() {
        return instance;
    }

    public TrophyManager getTrophyManager() {
        return trophyManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

}