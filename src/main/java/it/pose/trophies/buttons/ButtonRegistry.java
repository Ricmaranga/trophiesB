package it.pose.trophies.buttons;


import it.pose.trophies.Trophies;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ButtonRegistry {

    private static final Map<String, Consumer<ButtonClickContext>> actions = new HashMap<>();

    public static void register(String id, Consumer<ButtonClickContext> handler) {
        actions.put(id, handler);
    }

    public static Consumer<ButtonClickContext> getAction(String id) {
        return actions.get(id);
    }

    public static String getId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(Trophies.getInstance(), "button-id");
        return meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }
}
