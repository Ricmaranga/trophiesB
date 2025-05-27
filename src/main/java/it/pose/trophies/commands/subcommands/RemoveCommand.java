package it.pose.trophies.commands.subcommands;

import it.pose.trophies.Lang;
import it.pose.trophies.commands.SubCommand;
import it.pose.trophies.managers.PlayerDataManager;
import it.pose.trophies.managers.TrophyManager;
import org.bukkit.entity.Player;

import java.util.Map;


public class RemoveCommand extends SubCommand {
    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getSyntax() {
        return Lang.get("usage") + " /" + Lang.get("default-command") + "remove <player> <trophy>";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /trophies remove <player> <id|name>");
            return;
        }

        PlayerDataManager.removeTrophy(args[1], TrophyManager.getTrophyByName(args[2]));
        player.sendMessage(Lang.get("command.remove", Map.of("player", args[2])));
    }
}