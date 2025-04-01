package it.pose.trophies.gui;

import it.pose.trophies.Trophies;
import it.pose.trophies.Trophy;
import it.pose.trophies.managers.ConfigManager;
import it.pose.trophies.managers.PlayerDataManager;
import it.pose.trophies.managers.TrophyManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ShowcaseGUI {


    public static Inventory createShowcase(Player player) {

        final Trophies main = Trophies.getInstance();
        final TrophyManager trophyManager = main.getTrophyManager();
        final PlayerDataManager dataManager = main.getPlayerDataManager();
        final ConfigManager configManager = main.getConfigManager();
        final FileConfiguration config = configManager.getConfig();

        Map<Integer, Trophy> trophies = trophyManager.getAllTrophies();

        int slots = config.getInt("showcase-rows", 6) * 9;

        Inventory inv = Bukkit.createInventory(null, slots, config.getString("showcase-title", main.defaultTitle));

        Map<Integer, Boolean> playerData = dataManager.getPlayerData(player);


        if (playerData == null) {
            for (int slot = 0; slot < slots; slot++) {
                inv.setItem(slot, new ItemStack(configManager.getPlaceholderMaterial(slot)));
            }
        } else {
            for (int slot = 0; slot < slots; slot++) {
                if (trophies.containsKey(slot)) {
                    if (playerData.get(slot) != null) {
                        if (playerData.get(slot)) {
                            inv.setItem(slot, trophyManager.getTrophyItem(slot));
                        } else {
                            inv.setItem(slot, null);
                        }
                    } else {
                        inv.setItem(slot, null);
                    }
                } else {
                    inv.setItem(slot, new ItemStack(configManager.getPlaceholderMaterial(slot)));
                }
            }
        }
        return inv;
    }
}