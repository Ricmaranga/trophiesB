package it.pose.trophies.commands.subcommands;

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
        main.reloadConfig();
        trophyManager.reloadTrophies();
        playerDataManager.reloadPlayerData();
        player.sendMessage("§aPlugin reloaded!");
    }
}
