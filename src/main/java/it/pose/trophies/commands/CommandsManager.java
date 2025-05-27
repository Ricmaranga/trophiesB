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
                try {
                    player.openInventory(ShowcaseGUI.open(player));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
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
        if (!sender.hasPermission("trophies.admin")) return null;
        if (args.length == 1) return List.of(subcommand.stream().map(SubCommand::getName).toArray(String[]::new));

        if (args.length == 2) {
            switch (args[0]) {
                case "give", "purge":
                    return Trophies.getInstance().getServer().getOnlinePlayers().stream()
                            .map(Player::getName)
                            .toList();
            }
        }

        if (args.length == 3) {
            return switch (args[0]) {
                case "give" -> List.of(Trophies.trophies.values().stream().map(Trophy::getName).toArray(String[]::new));
                case "remove" -> PlayerDataManager.getTrophies(Bukkit.getPlayer(args[1]))
                        .keySet().stream()
                        .map(TrophyManager::getTrophy)
                        .map(Trophy::getName)
                        .toList();
                default -> Collections.singletonList("");
            };
        }

        return null;
    }
}
