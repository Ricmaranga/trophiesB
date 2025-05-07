package it.pose.trophies.buttons;

import it.pose.trophies.Trophies;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.function.Consumer;

public class ButtonCreator {

    private Material material;
    private String name;
    protected static Consumer<ButtonClickEvent> clickHandler;
    private final List<String> lore = new ArrayList<>();
    private static final Map<String, Consumer<ButtonClickContext>> clickHandlers = new HashMap<>();
    private final int amount = 1;
    public UUID buttonId;

    private ButtonCreator() {
    }

    public static class ButtonBuilder {
        private final ItemStack item;
        private final ItemMeta meta;

        public ButtonBuilder(Material material) {
            this.item = new ItemStack(material);
            this.meta = item.getItemMeta();
        }

        public ButtonBuilder name(String name) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            return this;
        }

        public ButtonBuilder lore(String loreLine) {
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            lore.add(ChatColor.translateAlternateColorCodes('&', loreLine));
            meta.setLore(lore);
            return this;
        }

        public ButtonBuilder lore(List<String> loreLines) {
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            for (String line : loreLines) {
                lore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(lore);
            return this;
        }

        public ItemStack build() {
            item.setItemMeta(meta);
            return item;
        }

        public ItemStack buildTrophy() {
            meta.addEnchant(Enchantment.INFINITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
            return item;
        }

        public ButtonBuilder onClick(String id, Consumer<ButtonClickContext> handler) {
            NamespacedKey key = new NamespacedKey(Trophies.getInstance(), "button-id");
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, id);
            ButtonRegistry.register(id, handler);
            
            return this;
        }
    }

    public ItemStack createItem() {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(new NamespacedKey(Trophies.getInstance(), "button-id"), PersistentDataType.STRING, buttonId.toString());
        item.setItemMeta(meta);
        return item;
    }

    public static class ButtonClickEvent {
        public final Player player;
        public final ClickType clickType;
        public final Inventory inventory;

        public ButtonClickEvent(Player player, ClickType clickType, Inventory inventory) {
            this.player = player;
            this.clickType = clickType;
            this.inventory = inventory;
        }
    }

    public static String getButtonId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;

        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(Trophies.getInstance(), "button-id");

        return meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }
}

