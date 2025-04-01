package it.pose.trophies.managers;

import it.pose.trophies.Trophies;
import it.pose.trophies.Trophy;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerDataManager {

    private final Trophies main = Trophies.getInstance();
    private final Map<UUID, Map<Integer, Boolean>> playerDataset = new HashMap<>();
    private Map<Integer, Boolean> playerTrophies = new HashMap<>();
    private YamlConfiguration playerDataConfig;
    private final TrophyManager trophyManager = main.getTrophyManager();

    public void loadPlayerData(UUID uuid) {

        File file = new File(main.getDataFolder() + "/playerData", uuid + ".yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        playerDataConfig = YamlConfiguration.loadConfiguration(file);

        main.getLogger().warning("Riga 36: " + trophyManager.getAllTrophies().keySet());

        for (Integer trophy : trophyManager.getAllTrophies().keySet()) {
            if (playerDataConfig.getBoolean(String.valueOf(trophy))) {
                playerTrophies.put(trophy, true);
            }
        }

        playerDataset.put(uuid, playerTrophies);

    }

    public void reloadPlayerData() {

        for (File file : new File(main.getDataFolder() + "/playerData").listFiles()) {
            main.getLogger().info(UUID.fromString(file.getName().replace(".yml", "")).toString());
            loadPlayerData(UUID.fromString(file.getName().replace(".yml", "")));
        }
    }

    public void savePlayerData(Player player, Map<Integer, Boolean> playerTrophies) {

        playerDataConfig = YamlConfiguration.loadConfiguration(new File(main.getDataFolder() + "/playerData", player.getUniqueId() + ".yml"));

        Set<Integer> keys = playerTrophies.keySet();

        for (Integer key : keys) {
            playerDataConfig.set(key.toString(), playerTrophies.get(key));
        }

        try {
            playerDataConfig.save(main.getDataFolder() + "/playerData/" + player.getUniqueId() + ".yml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<Integer, Boolean> getPlayerData(Player player) {

        return playerDataset.get(player.getUniqueId());
    }

    public void addPlayerData(Player player, Trophy trohpy) {

        playerTrophies = getPlayerData(player);
        playerDataset.remove(player.getUniqueId());
        playerTrophies.put(trohpy.slot, false);
        playerDataset.put(player.getUniqueId(), playerTrophies);
        savePlayerData(player, playerTrophies);
    }
}