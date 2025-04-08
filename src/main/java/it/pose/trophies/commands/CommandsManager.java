package it.pose.trophies.commands;

import it.pose.trophies.Trophies;
import it.pose.trophies.commands.subcommands.AdminCommand;
import it.pose.trophies.commands.subcommands.GiveCommand;
import it.pose.trophies.commands.subcommands.PlayerCommand;
import it.pose.trophies.commands.subcommands.ReloadCommand;
import it.pose.trophies.gui.ShowcaseGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandsManager implements CommandExecutor, TabExecutor {

    private final ArrayList<SubCommand> subcommand = new ArrayList<>();

    public CommandsManager() {
        subcommand.add(new ReloadCommand());
        subcommand.add(new PlayerCommand());
        subcommand.add(new AdminCommand());
        subcommand.add(new GiveCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player player) {

            if (args.length == 0) {
                player.openInventory(ShowcaseGUI.createShowcase(player));
                return true;
            }

            if (!player.hasPermission("trophies.admin")) return true;

            for (SubCommand sub : subcommand) {
                if (sub.getName().equalsIgnoreCase(args[0])) {
                    sub.perform(player, args);
                    return true;
                }
            }

            player.sendMessage("The command you just sent is not valid.");
        }


        return true;
    }


    public ArrayList<SubCommand> getSubcommand() {
        return subcommand;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("trophies.admin")) return null;
        if (args.length == 1) return List.of(subcommand.stream().map(SubCommand::getName).toArray(String[]::new));

        if (args.length == 2) {

            switch (args[0]) {
                case "give":
                    Trophies.getInstance().getTrophyManager().getAllTrophies();
                    return null;
                case "create", "remove":
                    return null;
                case "delete":
                    return List.of(subcommand.stream().map(SubCommand::getName).toArray(String[]::new));
            }
        }

        return null;

    }
}
