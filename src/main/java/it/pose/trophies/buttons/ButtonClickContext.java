package it.pose.trophies.buttons;

import it.pose.trophies.PluginGUIHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ButtonClickContext {
    public final Player player;
    public final InventoryClickEvent event;

    public ButtonClickContext(Player player, InventoryClickEvent rawEvent) {
        this.player = player;
        this.event = rawEvent;

        Inventory top = event.getView().getTopInventory();
        InventoryHolder holder = top.getHolder();

        if (!(holder instanceof PluginGUIHolder(String pluginId))) return;

        if (!"trophies".equals(pluginId)) return;
    }
}
