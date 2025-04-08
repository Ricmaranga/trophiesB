package it.pose.trophies.managers;

import it.pose.trophies.Trophies;
import it.pose.trophies.trophies.Trophy;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class TrophyManager {
    private static final Trophies main = Trophies.getInstance();
    private final File trophiesFile = new File(main.getDataFolder(), "trophies.yml");

    public TrophyManager() {
        reloadTrophies();
        loadTrophies();
    }

    public Trophy createNewTrophy() {
        Trophy trophy = new Trophy();
        main.trophies.put(trophy.getUUID(), trophy);
        return trophy;
    }

    public Trophy getTrophy(UUID uuid) {
        return main.trophies.get(uuid);
    }

    public Trophy getTrophy(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;

        NamespacedKey key = new NamespacedKey(main, "trophy-uuid");
        String uuidString = item.getItemMeta()
                .getPersistentDataContainer()
                .get(key, PersistentDataType.STRING);

        if (uuidString == null) return null;
        return getTrophy(UUID.fromString(uuidString));
    }

    public Trophy getTrophyBySlot(int slot) {
        for (Trophy trophy : main.trophies.values()) {
            if (trophy.getSlot() != null && trophy.getSlot() == slot) {
                return trophy;
            }
        }
        return null;
    }

    public Trophy getTrophy(int slot) {
        for (Trophy trophy : main.trophies.values()) {
            if (trophy.getSlot() == slot) return trophy;
        }
        return null;
    }

    public ItemStack getTrophyItem(Trophy trophy) {
        ItemStack item = new ItemStack(trophy.getMaterial());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            String name = trophy.getName();
            if (name != null)
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            List<String> lore = trophy.getLore();
            if (lore != null)
                meta.setLore(lore.stream()
                        .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                        .collect(Collectors.toList()));
            item.setItemMeta(meta);
        }

        return item;
    }

    public UUID getUUIDByName(String name) {
        for (Trophy trophy : main.trophies.values()) {
            if (trophy.getName().equals(name)) return trophy.getUUID();
        }
        return null;
    }

    public void saveTrophies() {
        YamlConfiguration config = new YamlConfiguration();

        // Only save dirty trophies
        List<Trophy> toSave = main.trophies.values().stream()
                .filter(Trophy::isDirty)
                .peek(Trophy::clearDirtyFlag)
                .toList();

        config.set("trophies", toSave);

        try {
            config.save(trophiesFile);
        } catch (Exception e) {
            main.getLogger().severe("Failed to save trophies: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadTrophies() {
        if (!trophiesFile.exists()) return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(trophiesFile);
        List<Map<String, Object>> trophyData =
                (List<Map<String, Object>>) config.getList("trophies");

        if (trophyData != null) {
            trophyData.forEach(data -> {
                Trophy trophy = Trophy.deserialize(data);
                main.trophies.put(trophy.getUUID(), trophy);
            });
        }
    }

    public void awardTrophy(Player player, UUID trophyUUID) {

        Trophy trophy = getTrophy(trophyUUID);

        ItemStack trophyItem = trophy.createItem();

        HashMap<Integer, ItemStack> leftovers = player.getInventory().addItem(trophyItem);

        if (!leftovers.isEmpty()) {
            player.getWorld().dropItemNaturally(player.getLocation(), trophyItem);
            player.sendMessage("§cInventory full! Trophy dropped at your feet.");
        } else {
            player.sendMessage("§aYou received the §e" + trophy.getName() + "§a trophy!");
        }

        player.updateInventory(); // Refresh inventory view

    }

    public boolean giveVerifiedTrophy(Player player, ItemStack item) {

        Trophy trophy = getTrophy(item);

        if (trophy != null) {
            player.getInventory().addItem(item).values().forEach(leftover -> {
                player.getWorld().dropItemNaturally(player.getLocation(), leftover);
            });
            return true;
        }
        return false;
    }

    public void awardTrophyToHotbar(Player player, UUID trophyUUID) {

        Trophy trophy = getTrophy(trophyUUID);

        if (trophy != null) {
            ItemStack trophyItem = trophy.createItem();
            PlayerInventory inv = player.getInventory();

            for (int i = 0; i < 9; i++) {
                if (inv.getItem(i) == null) {
                    inv.setItem(i, trophyItem);
                    player.updateInventory();
                    return;
                }
            }

            awardTrophy(player, trophyUUID);
        }
    }

    public Map<UUID, Trophy> getAllTrophies() {
        return main.trophies;
    }

    public void reloadTrophies() {
        File trophies = new File(Trophies.getInstance().getDataFolder(), "trophies.yml");
        FileConfiguration trophiesFile = YamlConfiguration.loadConfiguration(trophies);
        trophiesFile.options().copyDefaults(true);
    }
}