package it.pose.trophies.listeners;

import it.pose.trophies.Lang;
import it.pose.trophies.PluginGUIHolder;
import it.pose.trophies.Trophies;
import it.pose.trophies.buttons.ButtonClickContext;
import it.pose.trophies.buttons.ButtonRegistry;
import it.pose.trophies.gui.TrophyGUI;
import it.pose.trophies.inputs.ChatInputRegistry;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

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
        if (!(e.getWhoClicked() instanceof Player player)) return;

        Inventory inv = e.getInventory();

        InventoryHolder holder = inv.getHolder();
        if (!(holder instanceof PluginGUIHolder)) return;

        e.setCancelled(true);

        if (player.hasPermission("trophies.admin") && (e.getRawSlot() >= 27 && e.getRawSlot() <= 62) && e.getView().getTitle().equals(Lang.get("gui.create-title"))) {
            e.setCancelled(false);
            ItemStack clicked = e.getCurrentItem();
            UUID id = TrophyManager.getUUIDFromItem(clicked);

            if (id != null) {
                Trophy trophy = Trophies.trophies.get(id);
                if (trophy != null) {
                    trophy.setItem(clicked);
                }
            }
        }

        String id = ButtonRegistry.getId(e.getCurrentItem());
        if (id == null) return;

        Consumer<ButtonClickContext> handler = ButtonRegistry.getAction(id);
        if (handler != null) {
            handler.accept(new ButtonClickContext(player, e));
        }
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
        if (!(e.getPlayer() instanceof Player player)) return;
        if (!e.getView().getTitle().equals(Lang.get("gui.manage-title"))) return;

        if (!finalizingTrophies.remove(player.getUniqueId())) return;

        ItemStack item = e.getInventory().getItem(0);
        if (item == null || item.getType().isAir()) return;

        Trophy trophy = Trophy.fromItemStack(item);
        int slot = trophy.getSlot();

        if (slot < 0 || slot > 26) {
            player.sendMessage(Lang.get("trophy.invalidSlot"));
            Bukkit.getScheduler().runTaskLater(Trophies.getInstance(), () ->
                    player.openInventory(TrophyGUI.open(trophy)), 1L);
            return;
        }

        if (TrophyManager.isSlotOccupied(slot, trophy.getUUID())) {
            player.sendMessage(Lang.get("trophy.slot-already-taken"));
            Bukkit.getScheduler().runTaskLater(Trophies.getInstance(), () ->
                    player.openInventory(TrophyGUI.open(trophy)), 1L);
        }
    }
}