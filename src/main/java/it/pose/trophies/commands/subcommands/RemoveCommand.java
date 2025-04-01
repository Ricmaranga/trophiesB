package it.pose.trophies.commands.subcommands;

import it.pose.trophies.commands.SubCommand;
import org.bukkit.entity.Player;

public class RemoveCommand extends SubCommand {
    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return "Command to remove a trophy from a player";
    }

    @Override
    public String getSyntax() {
        return "/trophies remove <playerName> <trophyId, trophyName, all>";
    }

    @Override
    public void perform(Player player, String[] args) {

    }
}
