package it.pose.trophies.managers;

import it.pose.trophies.Trophies;
import it.pose.trophies.trophies.Trophy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

public class TrophyManager {

    private static final Trophies main = Trophies.getInstance();

    private static final Map<Integer, Trophy> trophiesBySlot = new HashMap<>();

    public TrophyManager() {
        reloadTrophies();
    }

    public static Trophy getTrophyByName(String input) {
        return main.trophies.get(UUID.fromString(input));
    }

    public Trophy getTrophy(UUID uuid) {
        return main.trophies.get(uuid);
    }

    public static Trophy getTrophy(int slot) {
        for (Trophy trophy : main.trophies.values()) {
            if (trophy.getSlot() != null && trophy.getSlot() == slot) {
                return trophy;
            }
        }
        return null;
    }

    public static Trophy getTrophyById(UUID uuid) {
        for (Trophy trophy : main.trophies.values()) {
            if (trophy.getUUID() == uuid) return trophy;
        }
        return null;
    }

    public UUID getUUIDByName(String name) {
        for (Trophy trophy : main.trophies.values()) {
            if (trophy.getName().equals(name)) return trophy.getUUID();
        }
        return null;
    }

    public static void saveTrophy(Trophy trophy) {
        FileConfiguration config = ConfigManager.getTrophiesConfig();
        String key = trophy.getUUID().toString();

        config.set("trophies." + key, trophy.serialize());

        ConfigManager.saveTrophiesConfig();

        if (!trophiesBySlot.containsKey(trophy.getSlot())) trophiesBySlot.put(trophy.getSlot(), trophy);
        if (!main.trophies.containsKey(trophy.getUUID())) main.trophies.put(trophy.getUUID(), trophy);

        if (trophy.isDirty()) trophy.clearDirtyFlag();
    }

    public static void loadTrophies() {
        main.trophies.clear();
        trophiesBySlot.clear();

        FileConfiguration config = ConfigManager.getTrophiesConfig();

        if (!config.isConfigurationSection("trophies")) return;

        ConfigurationSection section = config.getConfigurationSection("trophies");

        for (String key : section.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                ConfigurationSection trophyData = section.getConfigurationSection(key);

                Trophy trophy = Trophy.deserialize(trophyData.getValues(false));

                main.trophies.put(uuid, trophy);
                trophiesBySlot.put(trophy.getSlot(), trophy);

            } catch (Exception e) {
                Bukkit.getLogger().warning("[Trophies] Failed to load trophy with key: " + key);
                e.printStackTrace();
            }
        }

        checkSlotConflicts();

        Bukkit.getLogger().info("[Trophies] Loaded " + main.trophies.size() + " trophies.");
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

    public static void removeTrophyItemFromInventory(Player player, Trophy trophy) {
        ItemStack target = trophy.toItemStack();

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;

            if (itemsMatch(item, target)) {
                item.setAmount(item.getAmount() - 1);
                return;
            }
        }
    }

    public static void deleteTrophy(Trophy trophy) {
        UUID id = trophy.getUUID();
        int slot = trophy.getSlot();

        main.trophies.remove(id);
        trophiesBySlot.remove(slot);

        FileConfiguration config = ConfigManager.getTrophiesConfig();
        config.set("trophies." + id.toString(), null);
        ConfigManager.saveTrophiesConfig();

        PlayerDataManager.removeTrophyFromAllPlayers(id);
    }

    private static boolean itemsMatch(ItemStack a, ItemStack b) {
        if (a.getType() != b.getType()) return false;

        ItemMeta metaA = a.getItemMeta();
        ItemMeta metaB = b.getItemMeta();

        if (metaA == null || metaB == null) return false;

        if (!Objects.equals(metaA.getDisplayName(), metaB.getDisplayName())) return false;
        return Objects.equals(metaA.getLore(), metaB.getLore());
    }

    public static Map<UUID, Trophy> getAllTrophies() { return main.trophies; }

    public void reloadTrophies() {
        File trophies = new File(Trophies.getInstance().getDataFolder(), "trophies.yml");
        FileConfiguration trophiesFile = YamlConfiguration.loadConfiguration(trophies);
        trophiesFile.options().copyDefaults(true);
    }

    public static boolean checkSlot(int slot) { return (trophiesBySlot.containsKey(slot) || !((slot >= 0) && (slot <= 26))); }

    public static boolean isSlotOccupied(int slot, UUID exclude) {
        return main.trophies.values().stream()
                .anyMatch(t -> t.getSlot() == slot && !t.getUUID().equals(exclude));
    }

    private static void checkSlotConflicts() {
        Map<Integer, List<Trophy>> slotMap = new HashMap<>();

        for (Trophy trophy : main.trophies.values()) {
            slotMap.computeIfAbsent(trophy.getSlot(), s -> new ArrayList<>()).add(trophy);
        }

        for (Map.Entry<Integer, List<Trophy>> entry : slotMap.entrySet()) {
            List<Trophy> list = entry.getValue();
            if (list.size() > 1) {
                Bukkit.getLogger().warning("[Trophies] Slot conflict at slot " + entry.getKey() + ":");
                for (Trophy trophy : list) {
                    Bukkit.getLogger().warning(" - " + trophy.getName() + " (" + trophy.getUUID() + ")");
                }
            }
        }
    }
}