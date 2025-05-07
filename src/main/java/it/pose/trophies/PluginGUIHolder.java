package it.pose.trophies;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class PluginGUIHolder implements InventoryHolder {
    
    private final String pluginId;

    public PluginGUIHolder(String pluginId) {
        this.pluginId = pluginId;
    }

    public String getPluginId() {
        return pluginId;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
