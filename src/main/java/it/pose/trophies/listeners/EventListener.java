package it.pose.trophies.listeners;

import it.pose.trophies.Lang;
import it.pose.trophies.PluginGUIHolder;
import it.pose.trophies.Trophies;
import it.pose.trophies.gui.TrophyGUI;
import it.pose.trophies.inputs.ChatInputRegistry;
import it.pose.trophies.managers.ConfigManager;
import it.pose.trophies.managers.PlayerDataManager;
import it.pose.trophies.managers.TrophyManager;
import it.pose.trophies.trophies.Trophy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class EventListener implements Listener {

    private final Trophies main = Trophies.getInstance();
    private final PlayerDataManager dataManager = main.getPlayerDataManager();
    public static final Set<UUID> finalizingTrophies = new HashSet<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        dataManager.loadPlayer(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        PlayerDataManager.savePlayerData(player);
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (!(e.getInventory().getHolder() instanceof PluginGUIHolder)) {
            return;
        }

        e.setCancelled(true);

        boolean isAdmin = player.hasPermission("trophies.admin");
        boolean isManageGUI = e.getView().getTitle().equals(Lang.get("gui.manage"));
        int slot = e.getRawSlot();
        if (!isAdmin || slot < 27 || slot > ConfigManager.getConfig().getInt("showcase-rows") * 9 || !isManageGUI) {
            return;
        }

        e.setCancelled(false);

        Trophy trophy = Optional.ofNullable(e.getCurrentItem())
                .map(TrophyManager::getUUIDFromItem)
                .map(uuid -> Trophies.trophies.get(uuid))
                .orElse(null);

        if (trophy == null) {
            return;
        }

        trophy.setItem(e.getCurrentItem());
    }

    @EventHandler
    public void onTrophyPlace(BlockPlaceEvent e) {
        if (e.getBlock() instanceof Trophy) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cYou cannot place a trophy!");
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        if (ChatInputRegistry.has(player)) {
            e.setCancelled(true);
            String message = e.getMessage();

            Bukkit.getScheduler().runTask(Trophies.getInstance(), () -> ChatInputRegistry.handle(player, message));
        }
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        String message = e.getMessage();

        if (message.startsWith("/" + Lang.get("default-command"))) {
            e.setCancelled(true);

            String rerouted = "/trophies" + message.substring(("/" + Lang.get("default-command")).length());
            Bukkit.dispatchCommand(player, rerouted.substring(1));
        }

    }

    @EventHandler
    public void onTrophyGUIClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player player)) {
            return;
        }

        if (!e.getView().getTitle().equals(Lang.get("gui.manage-title"))) {
            return;
        }

        if (!finalizingTrophies.remove(player.getUniqueId())) {
            return;
        }

        ItemStack item = e.getInventory().getItem(0);
        if (item == null || item.getType().isAir()) {
            return;
        }

        Trophy trophy = Trophy.fromItemStack(item);
        int slot = trophy.getSlot();
        int maxSlot = ConfigManager.getConfig().getInt("showcase-rows") * 9;

        boolean invalidIndex = slot < 0 || slot > maxSlot;
        boolean occupied = !invalidIndex
                && TrophyManager.isSlotOccupied(slot, trophy.getUUID());

        if (invalidIndex || occupied) {
            player.sendMessage(Lang.get("trophy.invalidSlot"));
            Bukkit.getScheduler().runTaskLater(
                    Trophies.getInstance(),
                    () -> player.openInventory(TrophyGUI.open(trophy)),
                    1L
            );
        }
    }
}