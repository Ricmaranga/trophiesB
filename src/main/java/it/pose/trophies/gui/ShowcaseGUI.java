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
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.Map;

public class ShowcaseGUI implements Listener {

    public static int dim = ConfigManager.getConfig().getInt("showcase-rows") * 9;

    public static Inventory open(Player player) {
        Inventory gui = Bukkit.createInventory(new PluginGUIHolder("trophies"), dim, Lang.get("gui.showcase"));

        for (int slot = 0; slot < dim; slot++) {
            Trophy trophy = TrophyManager.getTrophy(slot);
            if (trophy == null) {
                gui.setItem(slot, getLockedItem(slot));
                continue;
            }

            if (!PlayerDataManager.hasUnlocked(player, trophy)) {
                gui.setItem(slot, getLockedItem(slot));
            } else if (PlayerDataManager.hasPlaced(player, trophy)) {
                ItemStack plain = trophy.toItemStack();
                ItemMeta meta = plain.getItemMeta();
                NamespacedKey key = new NamespacedKey(Trophies.getInstance(), "button-id");
                meta.getPersistentDataContainer().remove(key);
                plain.setItemMeta(meta);
                gui.setItem(slot, plain);
            } else {
                ItemStack needed = trophy.toItemStack();
                if (player.getInventory().containsAtLeast(needed, 1)) {
                    gui.setItem(slot, placeTrophy(trophy));
                } else {
                    ItemStack greyPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                    ItemMeta meta = greyPane.getItemMeta();
                    meta.setDisplayName("§7" + trophy.getName());
                    meta.setLore(Collections.singletonList("§8You don’t have this trophy"));
                    greyPane.setItemMeta(meta);

                    gui.setItem(slot, greyPane);
                }
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

    private static ItemStack placeTrophy(Trophy trophy) {
        return new ButtonCreator.ButtonBuilder(new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE))
                .name("§7" + trophy.getDisplayName())
                .lore(Lang.get("buttons.place.lore"))
                .onClick("place-" + trophy.getUUID(), e -> {
                    Player player = e.player;

                    ItemStack needed = trophy.toItemStack();
                    if (!player.getInventory().containsAtLeast(needed, 1)) {
                        player.sendMessage("§cYou need to have the trophy item in your inventory to place it!");
                        return;
                    }

                    if (PlayerDataManager.hasPlaced(player, trophy)) return;

                    player.getInventory().removeItem(needed);
                    PlayerDataManager.markPlaced(player, trophy);

                    player.closeInventory();
                    player.sendMessage(Lang.get("trophy.placed", Map.of("trophy", trophy.getName())));
                    player.openInventory(ShowcaseGUI.open(player));
                })
                .build();
    }
}