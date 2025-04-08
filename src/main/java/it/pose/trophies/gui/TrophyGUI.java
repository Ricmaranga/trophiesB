package it.pose.trophies.gui;

import it.pose.trophies.Trophies;
import it.pose.trophies.trophies.Trophy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TrophyGUI {

    private static final Trophies main = Trophies.getInstance();

    private static final FileConfiguration config = Trophies.getInstance().getConfigManager().getConfig();

    private static final String[] color = {"RED", "BLUE", "GREEN", "LIME", "ORANGE", "PINK", "GRAY", "LIGHT_BLUE", "MAGENTA", "PURPLE"};
    private static final ItemStack glassPanel = new ItemStack(Material.valueOf(color[(int) (Math.random() * color.length)] + "_STAINED_GLASS_PANE"));

    private static final String title = config.getString("adminGUI-title", "§6§lAdmin GUI");

    public static Inventory ListGUI(Trophy trophy) {

        int slots = 5 * 9;


        Inventory inv = Bukkit.createInventory(null, slots, title);

        for (int slot = 0; slot < slots; slot++) {
            if (slot <= 9 || slot == 17 || slot == 18 || slot == 26 || slot == 27 || slot >= 35)
                inv.setItem(slot, glassPanel);

        }

        return inv;
    }

}
