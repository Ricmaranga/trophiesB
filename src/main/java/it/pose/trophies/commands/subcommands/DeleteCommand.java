package it.pose.trophies.commands.subcommands;

import it.pose.trophies.Lang;
import it.pose.trophies.commands.SubCommand;
import it.pose.trophies.managers.TrophyManager;
import it.pose.trophies.trophies.Trophy;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class DeleteCommand extends SubCommand {
    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getSyntax() {
        return "/trophies delete <id|name>";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Lang.get("usage") + " /" + Lang.get("default-command") + " delete <id|name>");
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
            player.sendMessage(Lang.get("trophy.not-found"));
            return;
        }

        TrophyManager.deleteTrophy(trophy);
        player.sendMessage(Lang.get("trophy.deleted", Map.of("player", player.getName(),
                "trophy", trophy.getDisplayName())));
    }
}
