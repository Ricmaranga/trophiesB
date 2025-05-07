package it.pose.trophies.commands.subcommands;

import it.pose.trophies.Lang;
import it.pose.trophies.Trophies;
import it.pose.trophies.commands.SubCommand;
import it.pose.trophies.managers.PlayerDataManager;
import it.pose.trophies.managers.TrophyManager;
import org.bukkit.entity.Player;

public class ReloadCommand extends SubCommand {

    Trophies main = Trophies.getInstance();
    TrophyManager trophyManager = main.getTrophyManager();
    PlayerDataManager playerDataManager = main.getPlayerDataManager();


    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reload the plugin";
    }

    @Override
    public String getSyntax() {
        return "/trophies reload";
    }

    @Override
    public void perform(Player player, String[] args) {
        main.reloadConfig();                          // ✅ config.yml
        Lang.reload();                                  // ✅ lang_xx.yml
        main.getConfigManager().reloadTrophiesConfig();           // ✅ trophies.yml
        TrophyManager.loadTrophies();                   // ✅ refresh all trophies
        PlayerDataManager.loadAll();
        player.sendMessage("§a✔ Config reloaded");
        player.sendMessage("§a✔ Language loaded: §e" + Lang.getActiveLang());
        player.sendMessage("§a✔ Trophies: §e" + TrophyManager.getAllTrophies().size());
    }
}
