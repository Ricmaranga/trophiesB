package it.pose.trophies;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public record PluginGUIHolder(String pluginId) implements InventoryHolder {

    @Override
    public Inventory getInventory() {
        return null;
    }
}
