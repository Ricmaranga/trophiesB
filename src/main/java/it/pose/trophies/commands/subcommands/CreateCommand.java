package it.pose.trophies.commands.subcommands;

import it.pose.trophies.Trophies;
import it.pose.trophies.Trophy;
import it.pose.trophies.commands.SubCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Arrays;

public class CreateCommand extends SubCommand {
    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Command to create a new trophy";
    }

    @Override
    public String getSyntax() {
        return "/trophies create <slot> <trophyName>";
    }

    @Override
    public void perform(Player player, String[] args) {

        if (args.length < 3) {
            player.sendMessage("Usage /trophies create <slot> <trophyName>");
        } else {
            try {
                Integer slot = Integer.valueOf(args[1]);

                ItemStack handItem = player.getInventory().getItemInMainHand();

                String displayName = args[2];

                Trophy trophy = new Trophy(Integer.parseInt(args[1]), handItem, displayName);
                Trophies.getInstance().getTrophyManager().createTrophy(trophy, slot);

                player.sendMessage("§aTrophy created successfully!");

            } catch (NumberFormatException ex) {
                player.sendMessage("§cInvalid slot number!");
                Trophies.getInstance().getLogger().warning(ex.toString());
                Trophies.getInstance().getLogger().info(Arrays.toString(args));
            }
        }

        Trophies.getInstance().getTrophyManager().saveTrophies();

        for (File file : Trophies.getInstance().getDataFolder().listFiles()) {
            Trophies.getInstance().getPlayerDataManager().reloadPlayerData();
        }
    }
}

