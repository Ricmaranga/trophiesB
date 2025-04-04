package it.pose.trophies.buttons;

import it.pose.trophies.Trophies;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class ButtonCreator {

    private Material material;
    private String name;
    private Consumer<ButtonClickEvent> clickHandler;
    private List<String> lore = new ArrayList<>();
    private int amount = 1;
    public UUID buttonId;

    private ButtonCreator() {}

    public static class ButtonBuilder {
        private final ButtonCreator button;

        public ButtonBuilder() {
            this.button = new ButtonCreator();
        }

        public ButtonBuilder material(Material material) {
            button.material = material;
            return this;
        }

        public ButtonBuilder name(String name) {
            button.name = name;
            return this;
        }

        public ButtonBuilder onClick(Consumer<ButtonClickEvent> handler) {
            button.clickHandler = handler;
            return this;
        }

        public ButtonBuilder lore(String... lore) {
            button.lore = Arrays.asList(lore);
            return this;
        }

        public ButtonBuilder amount(int amount) {
            button.amount = amount;
            return this;
        }

        public ButtonCreator build() {
            if (button.material == null) throw new IllegalStateException("Material must be set");
            button.buttonId = UUID.randomUUID();
            ButtonRegistry.register(button);
            return button;
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

    public void handleClick(Player player, ClickType clickType, Inventory inventory) {
        if (clickHandler != null) {
            clickHandler.accept(new ButtonClickEvent(player, clickType, inventory));
        }
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

}
