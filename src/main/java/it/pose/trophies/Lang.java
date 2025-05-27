package it.pose.trophies;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lang {

    private static final Map<String, FileConfiguration> languages = new HashMap<>();
    private static String activeLang = "en";

    public static void init(JavaPlugin plugin) {
        File langFolder = new File(plugin.getDataFolder(), "languages");
        if (!langFolder.exists()) langFolder.mkdirs();

        List<String> builtinLangs = List.of("lang_en.yml", "lang_fr.yml", "lang_it.yml");

        for (String fileName : builtinLangs) {
            File target = new File(langFolder, fileName);
            if (!target.exists()) {
                plugin.saveResource("languages/" + fileName, false);
            }
        }

        File[] files = langFolder.listFiles((dir, name) -> name.startsWith("lang_") && name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            String code = file.getName().replace("lang_", "").replace(".yml", "");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            languages.put(code.toLowerCase(), config);
        }

        activeLang = plugin.getConfig().getString("language", "en").toLowerCase();
        if (!languages.containsKey(activeLang)) {
            plugin.getLogger().warning("[Lang] Language not found: " + activeLang + ", falling back to 'en'.");
            activeLang = "en";
        }
    }

    public static String get(String key) {
        FileConfiguration langFile = languages.getOrDefault(activeLang, languages.get("en"));
        String raw = langFile.getString(key);
        return ChatColor.translateAlternateColorCodes('&', raw != null ? raw : "§cMissing key: " + key);
    }

    public static String get(String key, Map<String, String> placeholders) {
        String msg = get(key);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            msg = msg.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        return msg;
    }

    public static String prefix() {
        return get("prefix") + " ";
    }

    public static String getActiveLang() {
        return activeLang;
    }

    public static void reload() {
        init(Trophies.getInstance());
    }
}