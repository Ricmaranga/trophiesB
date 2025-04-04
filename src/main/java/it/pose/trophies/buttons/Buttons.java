package it.pose.trophies.buttons;

import org.bukkit.Material;

public class Buttons {

    public static ButtonCreator closeButton = new ButtonCreator.ButtonBuilder()
            .material(Material.BARRIER)
            .name("§cClose Menu")
            .onClick(e -> e.player.closeInventory())
            .build();

    public static ButtonCreator nextPage = new ButtonCreator.ButtonBuilder()
            .material(Material.ARROW)
            .name("§aNext Page")
            .onClick(e -> e.player.sendMessage("Next page"))
            .build();

    public static ButtonCreator previousPage = new ButtonCreator.ButtonBuilder()
            .material(Material.GOLDEN_CARROT)
            .name("§9Previous page")
            .onClick(e -> e.player.sendMessage("Previous page"))
            .build();

    public static ButtonCreator createTrophy = new ButtonCreator.ButtonBuilder()
            .material(Material.BAMBOO)
            .name("§5Create trophy")
            .onClick(e -> e.player.sendMessage("Create trophy"))
            .build();
}
