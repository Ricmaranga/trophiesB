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

    private static final String[] COLORS = {
            "RED", "BLUE", "GREEN", "LIME", "ORANGE",
            "PINK", "GRAY", "LIGHT_BLUE", "MAGENTA", "PURPLE"
    };

    // Pick one random stained‐glass pane color once, at class‐load time.
    private static final ItemStack GLASS_PANEL =
            new ItemStack(Material.valueOf(COLORS[(int) (Math.random() * COLORS.length)] + "_STAINED_GLASS_PANE"));

    private static final String TITLE = Lang.get("gui.manage");

    public static Inventory open(Trophy trophy) {
        int slots = 3 * 9; // 27 slots total
        Inventory inv = Bukkit.createInventory(new PluginGUIHolder("trophies"), slots, TITLE);

        for (int slot = 0; slot < slots; slot++) {
            inv.setItem(slot, GLASS_PANEL);
        }

        inv.setItem(0, trophy.createItem());
        inv.setItem(10, Buttons.setName(trophy));
        inv.setItem(12, Buttons.setSlot(trophy));
        inv.setItem(14, Buttons.setMaterial(trophy));
        inv.setItem(16, Buttons.deleteTrophy(trophy));
        inv.setItem(18, Buttons.closeButton());
        inv.setItem(24, Buttons.goBack());

        return inv;
    }
}