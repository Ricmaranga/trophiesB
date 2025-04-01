package it.pose.trophies.commands.subcommands;

import it.pose.trophies.commands.SubCommand;
import org.bukkit.entity.Player;

public class ModifyCommand extends SubCommand {
    @Override
    public String getName() {
        return "modify";
    }

    @Override
    public String getDescription() {
        return "Command to modify a trophy";
    }

    @Override
    public String getSyntax() {
        return "/trophies modify <trophyId, trophyName>";
    }

    @Override
    public void perform(Player player, String[] args) {

    }
}
