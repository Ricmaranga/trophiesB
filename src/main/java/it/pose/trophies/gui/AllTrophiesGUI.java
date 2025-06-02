package it.pose.trophies.gui;

import it.pose.trophies.Lang;
import it.pose.trophies.PluginGUIHolder;
import it.pose.trophies.Trophies;
import it.pose.trophies.buttons.ButtonCreator;
import it.pose.trophies.managers.ConfigManager;
import it.pose.trophies.managers.TrophyManager;
import it.pose.trophies.trophies.Trophy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class AllTrophiesGUI {

    public static Inventory open(Player player) {
        Inventory gui = Bukkit.createInventory(new PluginGUIHolder("trophies"), ConfigManager.getConfig().getInt("showcase-rows") * 9, Lang.get("gui.list-title"));

        Trophies.getInstance().getLogger().warning(TrophyManager.getAllTrophies().values().toString());

        for (Trophy trophy : TrophyManager.getAllTrophies().values()) {
            if (trophy.getSlot() != -1) {
                ItemStack button = new ButtonCreator.ButtonBuilder(trophy.getMaterial())
                        .name(trophy.getDisplayName())
                        .lore(trophy.getLore())
                        .onClick("open-" + trophy.getUUID(), e -> {
                            e.player.closeInventory();
                            e.player.openInventory(TrophyGUI.open(trophy));
                        })
                        .buildTrophy();

                gui.setItem(trophy.getSlot(), button);
            } else {
                player.sendMessage(Lang.get("trophy.noSlot", Map.of("trophy", trophy.getName())));
            }
        }

        return gui;
    }
}
