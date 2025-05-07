package it.pose.trophies.gui;


import it.pose.trophies.Lang;
import it.pose.trophies.PluginGUIHolder;
import it.pose.trophies.Trophies;
import it.pose.trophies.buttons.ButtonCreator;
import it.pose.trophies.managers.ConfigManager;
import it.pose.trophies.managers.PlayerDataManager;
import it.pose.trophies.managers.TrophyManager;
import it.pose.trophies.trophies.Trophy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static it.pose.trophies.managers.TrophyManager.removeTrophyItemFromInventory;

public class ShowcaseGUI implements Listener {

    private static final ConfigManager configManager = Trophies.getInstance().getConfigManager();
    private static final FileConfiguration config = configManager.getConfig();

    public static Inventory open(Player player) {
        Inventory gui = Bukkit.createInventory(new PluginGUIHolder("trophies"), 27, Lang.get("gui.showcase-title"));

        for (int slot = 0; slot < 27; slot++) {
            Trophy trophy = TrophyManager.getTrophy(slot);
            if (trophy == null) {
                gui.setItem(slot, getLockedItem(slot));
                continue;
            }

            if (!PlayerDataManager.hasUnlocked(player, trophy)) {
                gui.setItem(slot, getLockedItem(slot));
            } else if (PlayerDataManager.hasPlaced(player, trophy)) {
                gui.setItem(slot, trophy.toItemStack());
            } else {
                gui.setItem(slot, getShadowItem(trophy));
            }
        }

        return gui;
    }

    private static ItemStack getLockedItem(int slot) {
        FileConfiguration config = Trophies.getInstance().getConfig();
        String path = "slots." + slot;

        String materialName = config.getString(path, "BARRIER");
        Material material = Material.matchMaterial(materialName);
        if (material == null) material = Material.BARRIER;

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§cLocked Trophy");
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getShadowItem(Trophy trophy) {
        return new ButtonCreator.ButtonBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
                .name("§7" + trophy.getName())
                .lore("§8The trophy goes here...")
                .onClick("place-" + trophy.getUUID(), e -> {
                    Player player = e.player;

                    if (PlayerDataManager.hasPlaced(player, trophy)) return;

                    PlayerDataManager.markPlaced(player, trophy);
                    player.closeInventory();
                    player.sendMessage("§aTrophy placed!");
                    removeTrophyItemFromInventory(player, trophy);
                    player.openInventory(ShowcaseGUI.open(player));
                })
                .build();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (!e.getView().getTitle().equals(Lang.get("gui.showcase-title"))) return;

        e.setCancelled(true);

        int slot = e.getRawSlot();
        Trophy trophy = TrophyManager.getTrophy(slot);
        if (trophy == null) return;

        if (!PlayerDataManager.hasUnlocked(player, trophy)) {
            player.sendMessage("§cYou haven't unlocked this trophy yet.");
            return;
        }

        if (PlayerDataManager.hasPlaced(player, trophy)) {
            player.sendMessage("§eThis trophy is already placed.");
            return;
        }

        PlayerDataManager.markPlaced(player, trophy);
        player.closeInventory();
        player.sendMessage("§aTrophy placed in your showcase!");
        player.openInventory(open(player));
    }

}