package it.pose.trophies.commands.subcommands;

import it.pose.trophies.commands.SubCommand;
import it.pose.trophies.managers.TrophyManager;
import it.pose.trophies.trophies.Trophy;
import org.bukkit.entity.Player;

import java.util.UUID;

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
        return "/trofei remove <player> <trophy>";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /trophies delete <id|name>");
            return;
        }

        String input = args[1];
        Trophy trophy = null;

        try {
            UUID uuid = UUID.fromString(input);
            trophy = TrophyManager.getTrophyById(uuid);
        } catch (IllegalArgumentException ignored) {
            trophy = TrophyManager.getTrophyByName(input);
        }

        if (trophy == null) {
            player.sendMessage("§cTrophy not found.");
            return;
        }

        TrophyManager.deleteTrophy(trophy);
        player.sendMessage("§aTrophy removed: §f" + trophy.getDisplayName());
    }
}

