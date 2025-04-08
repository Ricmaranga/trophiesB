package it.pose.trophies.gui;

import it.pose.trophies.Trophies;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static it.pose.trophies.buttons.Buttons.*;

public class AdminGUIs {

    private static final FileConfiguration config = Trophies.getInstance().getConfigManager().getConfig();

    private static final String[] color = {"RED", "BLUE", "GREEN", "LIME", "ORANGE", "PINK", "GRAY", "LIGHT_BLUE", "MAGENTA", "PURPLE"};
    private static final ItemStack glassPanel = new ItemStack(Material.valueOf(color[(int) (Math.random() * color.length)] + "_STAINED_GLASS_PANE"));

    private static final String title = config.getString("adminGUI-title", "§6§lAdmin GUI");

    public static Inventory createAdminGUI(Player player) {

        int slots = 5 * 9;


        Inventory inv = Bukkit.createInventory(null, slots, title);

        for (int slot = 0; slot < slots; slot++) {
            if (slot <= 9 || slot == 17 || slot == 18 || slot == 26 || slot == 27 || slot >= 35)
                inv.setItem(slot, glassPanel);
            if (slot == 11) inv.setItem(slot, createTrophy.createItem());
            if (slot == 42) inv.setItem(slot, previousPage.createItem());
            if (slot == 44) inv.setItem(slot, nextPage.createItem());
            if (slot == 36) inv.setItem(slot, closeButton.createItem());
        }

        // mancano create item, modify, delete

        return inv;
    }

}
