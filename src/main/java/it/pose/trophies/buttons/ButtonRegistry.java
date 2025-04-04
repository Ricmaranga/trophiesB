package it.pose.trophies.buttons;

import it.pose.trophies.Trophies;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ButtonRegistry {
    private static final Map<UUID, ButtonCreator> buttons = new HashMap<>();

    public static void register(ButtonCreator button) {
        buttons.put(button.buttonId, button);
    }

    public static ButtonCreator get(ItemStack item) {

        NamespacedKey key = new NamespacedKey(Trophies.getInstance(), "button-id");
        ItemMeta meta = item.getItemMeta();

        String uuidString = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        if (uuidString == null) return null;

        return buttons.get(UUID.fromString(uuidString));
    }
}