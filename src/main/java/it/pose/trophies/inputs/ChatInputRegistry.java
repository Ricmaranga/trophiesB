package it.pose.trophies.inputs;

import it.pose.trophies.Lang;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ChatInputRegistry {
    private static final Map<UUID, Consumer<String>> listeners = new HashMap<>();

    public static void waitFor(Player player, Consumer<String> callback) {
        listeners.put(player.getUniqueId(), callback);
        player.sendMessage(ChatColor.YELLOW + "✏️ " + Lang.get("player.input.lore"));
        player.sendMessage(ChatColor.GRAY + Lang.get("player.input.cancel"));
    }

    public static void handle(Player player, String message) {
        UUID uuid = player.getUniqueId();
        Consumer<String> callback = listeners.remove(uuid);

        if (callback == null) return;

        if (message.equalsIgnoreCase("cancel")) {
            player.sendMessage(ChatColor.RED + Lang.get("player.input.canceled"));
        } else {
            callback.accept(message);
        }
    }

    public static boolean has(Player player) {
        return listeners.containsKey(player.getUniqueId());
    }
}