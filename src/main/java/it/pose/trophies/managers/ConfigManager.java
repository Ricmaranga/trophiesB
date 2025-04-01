package it.pose.trophies.managers;

import it.pose.trophies.Trophies;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigManager {

    private FileConfiguration config;

    public ConfigManager() {
        reloadConfig();
    }

    public void reloadConfig() {
        File configFile = new File(Trophies.getInstance().getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
        config.options().copyDefaults(true);
    }

    public Material getPlaceholderMaterial(int slot) {
        String materialName = config.getString("slots." + slot, "slots.default");
        try {
            return Material.valueOf(materialName);
        } catch (IllegalArgumentException e) {
            return Material.GRAY_STAINED_GLASS_PANE;
        }
    }

    public void saveDefaultConfig() {
        File configFile = new File(Trophies.getInstance().getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            Trophies.getInstance().saveResource("config.yml", false);
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }
}