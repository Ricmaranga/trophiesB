package it.pose.trophies.gui;

import it.pose.trophies.Lang;
import it.pose.trophies.PluginGUIHolder;
import it.pose.trophies.buttons.Buttons;
import it.pose.trophies.trophies.Trophy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TrophyGUI {

    private static final String[] color = {"RED", "BLUE", "GREEN", "LIME", "ORANGE", "PINK", "GRAY", "LIGHT_BLUE", "MAGENTA", "PURPLE"};
    private static final ItemStack glassPanel = new ItemStack(Material.valueOf(color[(int) (Math.random() * color.length)] + "_STAINED_GLASS_PANE"));

    private static final String title = Lang.get("gui.manage");

    public static Inventory open(Trophy trophy) {

        int slots = 3 * 9;

        Inventory inv = Bukkit.createInventory(new PluginGUIHolder("trophies"), slots, title);

        for (int slot = 0; slot < slots; slot++) {
            if (slot <= 9 || slot >= 17)
                inv.setItem(slot, glassPanel);
            if (slot == 0) inv.setItem(slot, trophy.createItem());
            if (slot == 10) inv.setItem(slot, Buttons.setName(trophy));
            if (slot == 12) inv.setItem(slot, Buttons.setSlot(trophy));
            if (slot == 14) inv.setItem(slot, Buttons.setMaterial(trophy));
            if (slot == 16) inv.setItem(slot, Buttons.deleteTrophy(trophy));
            if (slot == 18) inv.setItem(slot, Buttons.closeButton());
            if (slot == 24) inv.setItem(slot, Buttons.goBack());
        }

        return inv;
    }
}
