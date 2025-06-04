package it.pose.trophies.commands;

import it.pose.trophies.Lang;
import it.pose.trophies.Trophies;
import it.pose.trophies.commands.subcommands.*;
import it.pose.trophies.gui.ShowcaseGUI;
import it.pose.trophies.managers.PlayerDataManager;
import it.pose.trophies.managers.TrophyManager;
import it.pose.trophies.trophies.Trophy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CommandsManager implements CommandExecutor, TabExecutor {

    private final ArrayList<SubCommand> subcommand = new ArrayList<>();

    public CommandsManager() {
        subcommand.add(new ReloadCommand());
        subcommand.add(new PurgeCommand());
        subcommand.add(new AdminCommand());
        subcommand.add(new GiveCommand());
        subcommand.add(new RemoveCommand());
        subcommand.add(new HelpCommand());
        subcommand.add(new DeleteCommand());
        subcommand.add(new PlayerCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player player) {

            if (args.length == 0) {
                player.openInventory(ShowcaseGUI.open(player));
                return true;
            }

            if (!player.hasPermission("trophies.admin")) {
                Lang.get("player.no-permission");
                return true;
            }

            for (SubCommand sub : subcommand) {
                if (sub.getName().equalsIgnoreCase(args[0])) {
                    sub.perform(player, args);
                    return true;
                }
            }

            player.sendMessage(Lang.get("player.invalid-command"));
        }


        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("trophies.admin")) {
            return null;
        }

        int length = args.length;

        if (length == 1) {
            return subcommand.stream()
                    .map(SubCommand::getName)
                    .toList();
        }

        if (length == 2) {
            String firstArg = args[0];
            if (firstArg.equals("give") || firstArg.equals("purge")) {
                return Trophies.getInstance()
                        .getServer()
                        .getOnlinePlayers()
                        .stream()
                        .map(Player::getName)
                        .toList();
            }
            return Collections.emptyList();
        }

        if (length == 3) {
            String firstArg = args[0];

            // 4a) "give" expects a trophy name
            if (firstArg.equals("give")) {
                return Trophies.trophies.values()
                        .stream()
                        .map(Trophy::getName)
                        .toList();
            }

            if (firstArg.equals("remove")) {
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    return Collections.emptyList();
                }
                return PlayerDataManager.getTrophies(target)
                        .keySet()
                        .stream()
                        .map(TrophyManager::getTrophy)
                        .filter(Objects::nonNull)
                        .map(Trophy::getName)
                        .distinct()
                        .toList();
            }

            return Collections.emptyList();
        }

        return null;
    }
}
