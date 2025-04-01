package it.pose.trophies.commands.subcommands;

import it.pose.trophies.commands.SubCommand;
import org.bukkit.entity.Player;

public class DeleteCommand extends SubCommand {
    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getDescription() {
        return "Command to delete a trophy";
    }

    @Override
    public String getSyntax() {
        return "/trophies delete <trophyId, trophyName>";
    }

    @Override
    public void perform(Player player, String[] args) {

    }
}
