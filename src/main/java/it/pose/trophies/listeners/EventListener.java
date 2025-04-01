package it.pose.trophies.listeners;

import de.tr7zw.nbtapi.NBTItem;
import it.pose.trophies.Trophies;
import it.pose.trophies.Trophy;
import it.pose.trophies.managers.ConfigManager;
import it.pose.trophies.managers.PlayerDataManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

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

        e.setCancelled(true);

        if (!e.getView().getTitle().equals(config.getString("showcase-title", main.defaultTitle))) return;


        // e.getAction() == InventoryAction.PLACE_ALL return;
        if (!(e.getWhoClicked() instanceof Player player)) return;

        ItemStack cursorItem = e.getCurrentItem();
        if (cursorItem == null) return;

        NBTItem nbtItem = new NBTItem(cursorItem);
        if (!nbtItem.hasTag("trophyId")) return;

        Integer trophySlot = nbtItem.getInteger("trophyId");
        Trophy trophy = main.getTrophyManager().getTrophy(nbtItem.getInteger("trophyId"));

        if (trophy == null) return;
        main.getLogger().info("Qui va 6");

        Map<Integer, Boolean> playerData = dataManager.getPlayerData(player);

        main.getLogger().info(playerData.get(trophySlot).toString());

        if (!playerData.get(trophySlot)) {
            player.sendMessage("§cYou haven't unlocked this trophy!");
            return;
        }


        if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {

            ItemStack trophyPlaced = cursorItem.clone();
            ItemMeta meta = trophyPlaced.getItemMeta();
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.values());
            trophyPlaced.setItemMeta(meta);

            player.setItemOnCursor(null);

            playerData.put(trophySlot, true);
            dataManager.savePlayerData(player, playerData);
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
