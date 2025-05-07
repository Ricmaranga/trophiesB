package it.pose.trophies.managers;

import it.pose.trophies.Trophies;
import it.pose.trophies.trophies.Trophy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private static final File dataFolder = new File(Trophies.getInstance().getDataFolder(), "playerData");
    private static FileConfiguration data;

    private static final Map<UUID, Map<UUID, Boolean>> playerTrophies = new HashMap<>();

    public static void init() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

    }

    public static Map<UUID, Boolean> loadPlayerData(UUID uuid) {
        File folder = new File(Trophies.getInstance().getDataFolder(), "playerData");
        if (!folder.exists()) {
            folder.mkdirs(); // just in case
        }

        File file = new File(folder, uuid.toString() + ".yml");
        if (!file.exists()) {
            return new HashMap<>();
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        Map<UUID, Boolean> data = new HashMap<>();

        ConfigurationSection section = config.getConfigurationSection("unlocked");
        if (section != null) {
            for (String trophyId : section.getKeys(false)) {
                try {
                    UUID trophyUuid = UUID.fromString(trophyId);
                    Boolean state = section.getBoolean(trophyId);
                    data.put(trophyUuid, state);
                } catch (IllegalArgumentException ignored) {
                    Bukkit.getLogger().warning("Invalid UUID in " + Bukkit.getPlayer(uuid).getName() + "'s data: " + trophyId);
                }
            }
        }

        return data;
    }

    public static void savePlayerData(Player player) {
        UUID playerId = player.getUniqueId();
        Map<UUID, Boolean> trophies = playerTrophies.get(playerId);
        if (trophies == null) return;

        File folder = new File(Trophies.getInstance().getDataFolder(), "playerData");
        if (!folder.exists()) folder.mkdirs();

        File file = new File(folder, playerId.toString() + ".yml");
        FileConfiguration config = new YamlConfiguration();

        for (Map.Entry<UUID, Boolean> entry : trophies.entrySet()) {
            config.set("unlocked." + entry.getKey().toString(), entry.getValue());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to save data for " + player.getName());
            e.printStackTrace();
        }
    }

    public static void savePlayerData(UUID playerId, Map<UUID, Boolean> trophies) {
        File folder = new File(Trophies.getInstance().getDataFolder(), "playerData");
        if (!folder.exists()) folder.mkdirs();

        File file = new File(folder, playerId.toString() + ".yml");
        FileConfiguration config = new YamlConfiguration();

        for (Map.Entry<UUID, Boolean> entry : trophies.entrySet()) {
            config.set("unlocked." + entry.getKey().toString(), entry.getValue());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to save data for UUID: " + playerId);
            e.printStackTrace();
        }
    }

    public static void loadAll() {
        playerTrophies.clear();

        File folder = new File(Trophies.getInstance().getDataFolder(), "playerData");
        if (!folder.exists()) {
            folder.mkdirs();
            return;
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            String fileName = file.getName().replace(".yml", "");
            UUID playerId;
            try {
                playerId = UUID.fromString(fileName);
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning("[Trophies] Skipping invalid player file: " + fileName);
                continue;
            }

            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection section = config.getConfigurationSection("unlocked");
            if (section == null) continue;

            Map<UUID, Boolean> trophies = new HashMap<>();
            for (String trophyId : section.getKeys(false)) {
                try {
                    UUID trophyUUID = UUID.fromString(trophyId);
                    Boolean state = section.getBoolean(trophyId);
                    trophies.put(trophyUUID, state);
                } catch (IllegalArgumentException ignored) {
                    Bukkit.getLogger().warning("[Trophies] Invalid trophy UUID in " + fileName + ": " + trophyId);
                }
            }

            playerTrophies.put(playerId, trophies);
        }

        Bukkit.getLogger().info("[Trophies] Loaded data for " + playerTrophies.size() + " players.");
    }

    public static void saveAll() {
        for (UUID playerId : playerTrophies.keySet()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                savePlayerData(player);
            } else {
                // still save offline player's cached data
                savePlayerData(playerId, playerTrophies.get(playerId));
            }
        }
    }

    public void reloadAll() {
        playerTrophies.clear();
        loadAll();
    }

    public static Map<UUID, Boolean> getTrophiesFor(UUID playerId) {
        return playerTrophies.getOrDefault(playerId, new HashMap<>());
    }

    public static Map<UUID, Boolean> getTrophies(Player player) {
        return playerTrophies.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
    }

    public void loadPlayer(UUID uuid) {
        Map<UUID, Boolean> trophies = new HashMap<>();
        File file = new File(Trophies.getInstance().getDataFolder(), "playerData/" + uuid.toString() + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        data = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection section = data.getConfigurationSection("unlocked");
        if (section != null) {
            for (String trophyId : section.getKeys(false)) {
                trophies.put(UUID.fromString(trophyId), section.getBoolean(trophyId));
            }
        }

        playerTrophies.put(uuid, trophies);
    }


    public static void unlockTrophy(Player player, Trophy trophy, boolean placed) {
        Map<UUID, Boolean> data = getTrophies(player);
        data.put(trophy.getUUID(), placed ? true : false);
        savePlayerData(player);
    }

    public static boolean hasUnlocked(Player player, Trophy trophy) {
        return getTrophies(player).containsKey(trophy.getUUID());
    }

    public static boolean hasPlaced(Player player, Trophy trophy) {
        return getTrophies(player).get(trophy.getUUID());
    }


    public static void markPlaced(Player player, Trophy trophy) {
        Map<UUID, Boolean> data = getTrophies(player);
        if (data.containsKey(trophy.getUUID())) {
            data.put(trophy.getUUID(), true);
            savePlayerData(player);
        }
    }

    public static void removeTrophy(Player player, Trophy trophy) {
        Map<UUID, Boolean> map = playerTrophies.get(player.getUniqueId());
        if (map != null) {
            map.remove(trophy.getUUID());
        }
    }

    public static void removeTrophyFromAllPlayers(UUID trophyId) {
        for (Map.Entry<UUID, Map<UUID, Boolean>> entry : playerTrophies.entrySet()) {
            entry.getValue().remove(trophyId);
            savePlayerData(entry.getKey(), entry.getValue());
        }
    }
}