package it.pose.trophies.commands.subcommands;

import it.pose.trophies.commands.SubCommand;
import org.bukkit.entity.Player;

public class HelpCommand extends SubCommand {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getSyntax() {
        return "/trophies help";
    }

    @Override
    public void perform(Player player, String[] args) {

    }
}
