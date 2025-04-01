package it.pose.trophies.commands.subcommands;

import it.pose.trophies.commands.SubCommand;
import org.bukkit.entity.Player;

public class ListCommand extends SubCommand {
    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "Command to list all the trophies";
    }

    @Override
    public String getSyntax() {
        return "/trophies list";
    }

    @Override
    public void perform(Player player, String[] args) {

    }
}
