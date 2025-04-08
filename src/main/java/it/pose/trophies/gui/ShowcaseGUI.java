package it.pose.trophies.gui;

import it.pose.trophies.Trophies;
import it.pose.trophies.managers.ConfigManager;
import it.pose.trophies.managers.PlayerDataManager;
import it.pose.trophies.managers.TrophyManager;
import it.pose.trophies.trophies.Trophy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class ShowcaseGUI {


    public static Inventory createShowcase(Player player) {

        final Trophies main = Trophies.getInstance();
        final TrophyManager trophyManager = main.getTrophyManager();
        final PlayerDataManager dataManager = main.getPlayerDataManager();
        final ConfigManager configManager = main.getConfigManager();
        final FileConfiguration config = configManager.getConfig();

        Map<UUID, Trophy> trophies = trophyManager.getAllTrophies();

        int slots = config.getInt("showcase-rows", 6) * 9;

        Inventory inv = Bukkit.createInventory(null, slots, config.getString("showcase-title", main.defaultTitle));

        Map<UUID, Boolean> playerData = dataManager.getPlayerData(player);


        if (playerData == null) {
            for (int slot = 0; slot < slots; slot++) {
                inv.setItem(slot, new ItemStack(configManager.getPlaceholderMaterial(slot)));
            }
        } else {
            for (int slot = 0; slot < slots; slot++) {
                if (trophyManager.getTrophyBySlot(slot) != null) {
                    Boolean value = playerData.get(trophyManager.getTrophyBySlot(slot).getUUID());
                    if (value != null) {
                        if (value) {
                            inv.setItem(slot, trophyManager.getTrophyBySlot(slot).createItem());
                        } else {
                            inv.setItem(slot, null);
                        }
                    }
                } else {
                    inv.setItem(slot, new ItemStack(configManager.getPlaceholderMaterial(slot)));
                }
            }
        }
        return inv;
    }
}