package it.pose.trophies.listeners;

import it.pose.trophies.Trophies;
import it.pose.trophies.buttons.ButtonCreator;
import it.pose.trophies.buttons.ButtonRegistry;
import it.pose.trophies.gui.AdminGUIs;
import it.pose.trophies.managers.ConfigManager;
import it.pose.trophies.managers.PlayerDataManager;
import it.pose.trophies.trophies.Trophy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener {

    private final Trophies main = Trophies.getInstance();
    private final ConfigManager configManager = main.getConfigManager();
    private final FileConfiguration config = configManager.getConfig();
    private Player player;
    private final PlayerDataManager dataManager = main.getPlayerDataManager();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        player = e.getPlayer();

        dataManager.loadPlayerData(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerJoinEvent e) {
        player = e.getPlayer();
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {


        if (e.getInventory().getType().equals(InventoryType.ANVIL)) {
            e.setCancelled(true);
            if (e.getRawSlot() == 2) {
                ItemStack result = e.getCurrentItem();
                if (result != null && result.hasItemMeta()) {
                    Trophy trophy = new Trophy();
                    trophy.setName(result.getItemMeta().getDisplayName());
                    main.trophies.put(trophy.getUUID(), trophy);
                }
                e.getWhoClicked().closeInventory();
                e.getWhoClicked().openInventory(AdminGUIs.createAdminGUI((Player) e.getWhoClicked()));
            }
            return;
        }

        ItemStack clickedItem = e.getCurrentItem();
        ButtonCreator button = ButtonRegistry.get(clickedItem);

        if (button != null) {
            button.handleClick((Player) e.getWhoClicked(), e.getClick(), e.getInventory());
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTitle().equals(config.getString("showcase-title", main.defaultTitle))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTrophyPlace(BlockPlaceEvent e) {
        if (e.getBlock() instanceof Trophy) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cYou cannot place a trophy!");
        }
    }

}
