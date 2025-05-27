package it.pose.trophies.gui;

import it.pose.trophies.Lang;
import it.pose.trophies.PluginGUIHolder;
import it.pose.trophies.buttons.Buttons;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AdminGUI {

    private static final String[] color = {"RED", "BLUE", "GREEN", "LIME", "ORANGE", "PINK", "GRAY", "LIGHT_BLUE", "MAGENTA", "PURPLE"};
    private static final ItemStack glassPanel = new ItemStack(Material.valueOf(color[(int) (Math.random() * color.length)] + "_STAINED_GLASS_PANE"));

    public static Inventory open() {

        int slots = 3 * 9;

        Inventory inv = Bukkit.createInventory(new PluginGUIHolder("trophies"), slots, Lang.get("gui.admin-title"));

        for (int slot = 0; slot < slots; slot++) {
            if (slot <= 9 || slot >= 17)
                inv.setItem(slot, glassPanel);
            if (slot == 12) inv.setItem(slot, Buttons.createTrophy());
            if (slot == 14) inv.setItem(slot, Buttons.listAllTrophies());
            if (slot == 18) inv.setItem(slot, Buttons.closeButton());
        }

        return inv;
    }
}