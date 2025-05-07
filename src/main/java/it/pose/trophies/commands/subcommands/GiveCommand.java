package it.pose.trophies.commands.subcommands;

import it.pose.trophies.Lang;
import it.pose.trophies.Trophies;
import it.pose.trophies.commands.SubCommand;
import it.pose.trophies.managers.PlayerDataManager;
import it.pose.trophies.managers.TrophyManager;
import it.pose.trophies.trophies.Trophy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class GiveCommand extends SubCommand {

    private final Trophies main = Trophies.getInstance();
    private final TrophyManager trophyManager = main.getTrophyManager();
    private final PlayerDataManager playerDataManager = main.getPlayerDataManager();

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "Give a trophy to a player";
    }

    @Override
    public String getSyntax() {
        return "/trophies give <player> <trophyName>";
    }

    @Override
    public void perform(Player player, String[] args) {
        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            player.sendMessage("§cPlayer not found!");
        }


        TrophyManager trophyManager = Trophies.getInstance().getTrophyManager();
        UUID uuid = trophyManager.getUUIDByName(args[2]);
        Trophy trophy = trophyManager.getTrophy(uuid);

        if (trophy == null) {
            player.sendMessage("§cTrophy not found!");
        }

        trophyManager.awardTrophyToHotbar(target, uuid);

        PlayerDataManager.unlockTrophy(target, trophy, false);


        player.sendMessage(Lang.get("player.trophy-given", Map.of("player", args[1])));
    }
}
