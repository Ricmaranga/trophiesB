package it.pose.trophies.gui;

import it.pose.trophies.Trophies;
import it.pose.trophies.trophies.Trophy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class SetVariablesGUI {

    public static Inventory openAnvilInput(Player player, Trophy trophy) {

        Inventory anvil = Bukkit.createInventory(player, InventoryType.ANVIL, "Enter trophy name");
        anvil.setItem(0, Trophies.getInstance().getTrophyManager().getTrophyItem(trophy));

        return anvil;
    }
}
