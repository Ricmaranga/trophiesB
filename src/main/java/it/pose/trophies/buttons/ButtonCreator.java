package it.pose.trophies.buttons;

import it.pose.trophies.Trophies;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ButtonCreator {

    private ButtonCreator() {
    }

    public static class ButtonBuilder {
        private final ItemStack item;
        private final ItemMeta meta;

        public ButtonBuilder(ItemStack item) {
            this.item = item;
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
}

