package it.pose.trophies.managers;

import it.pose.trophies.Trophies;
import it.pose.trophies.Trophy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TrophyManager {

    private FileConfiguration trophiesConfig;
    private final Map<Integer, Trophy> trophies = new HashMap<>();

    public void reloadTrophies() {
        File trophiesFile = new File(Trophies.getInstance().getDataFolder(), "trophies.yml");

        if (!trophiesFile.exists()) {
            Trophies.getInstance().saveResource("trophies.yml", false);
        }

        trophiesConfig = YamlConfiguration.loadConfiguration(trophiesFile);
        trophiesConfig.options().copyDefaults(true);
        loadTrophies();
    }

    private void loadTrophies() {
        trophies.clear();
        for (String slot : trophiesConfig.getKeys(false)) {
            Map<String, Object> trophyData = trophiesConfig.getConfigurationSection(slot).getValues(false);
            Trophy trophy = Trophy.deserialize(Integer.parseInt(slot), trophyData);

            trophies.put(Integer.parseInt(slot), trophy);
        }
    }

    public void saveTrophies() {
        try {
            trophiesConfig.save(new File(Trophies.getInstance().getDataFolder(), "trophies.yml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createTrophy(Trophy trophy, Integer slot) {
        trophiesConfig.set(String.valueOf(slot), trophy.serialize());
        saveTrophies();

        trophies.put(slot, trophy);
    }

    public Trophy getTrophy(Integer id) {
        return trophies.get(id);
    }

    public Map<Integer, Trophy> getAllTrophies() {
        return trophies;
    }

    public ItemStack getTrophyItem(Integer id) {
        Trophy trophy = getTrophy(id);
        if (trophy == null) {
            return null;
        }
        return trophy.item();
    }

    public void giveTrophy(Trophy trophy, Player player) {
        player.getInventory().addItem(trophy.item());
    }

}