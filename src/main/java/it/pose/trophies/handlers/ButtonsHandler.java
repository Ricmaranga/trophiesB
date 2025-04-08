package it.pose.trophies.handlers;

import it.pose.trophies.Trophies;
import it.pose.trophies.buttons.ButtonCreator;
import it.pose.trophies.gui.SetVariablesGUI;
import it.pose.trophies.managers.TrophyManager;
import it.pose.trophies.trophies.Trophy;

public class ButtonsHandler {

    private final TrophyManager trophyManager = Trophies.getInstance().getTrophyManager();

    public void createTrophy(ButtonCreator.ButtonClickEvent e) {
        Trophy trophy = new Trophy();
        e.player.openInventory(SetVariablesGUI.openAnvilInput(e.player, trophy));
        // e.player.getInventory().addItem(trophy.createItem());
    }
}
