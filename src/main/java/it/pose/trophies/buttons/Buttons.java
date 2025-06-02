package it.pose.trophies.buttons;

import it.pose.trophies.ItemSerialization;
import it.pose.trophies.Lang;
import it.pose.trophies.Trophies;
import it.pose.trophies.gui.AdminGUI;
import it.pose.trophies.gui.AllTrophiesGUI;
import it.pose.trophies.gui.TrophyGUI;
import it.pose.trophies.inputs.ChatInputRegistry;
import it.pose.trophies.listeners.EventListener;
import it.pose.trophies.managers.ConfigManager;
import it.pose.trophies.managers.TrophyManager;
import it.pose.trophies.trophies.Trophy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public class Buttons {

    public static ItemStack closeButton() {
        return new ButtonCreator.ButtonBuilder(new ItemStack(Material.BARRIER))
                .name("§cClose Menu")
                .onClick("close", e -> e.player.closeInventory())
                .build();
    }

    public static ItemStack createTrophy() {
        return new ButtonCreator.ButtonBuilder(new ItemStack(Material.BAMBOO))
                .name("§5Create trophy")
                .onClick("createTrophy", e -> {
                    e.player.closeInventory();
                    Trophy trophy = new Trophy();
                    ChatInputRegistry.waitFor(
                            e.player,
                            List.of("id"),
                            input -> {
                                trophy.setName(ChatColor.translateAlternateColorCodes('&', input));
                                e.player.sendMessage(trophy.toString());
                                e.player.sendMessage(trophy.getDisplayName());
                                e.player.sendMessage(ChatColor.GREEN + "Trophy name set to: " + input);
                                e.player.openInventory(TrophyGUI.open(trophy));
                                TrophyManager.saveTrophy(trophy);
                            });
                })
                .build();
    }

    public static ItemStack listAllTrophies() {
        return new ButtonCreator.ButtonBuilder(new ItemStack(Material.BOOK))
                .name("§9Trophies list")
                .onClick("listTrophies", e -> {
                    e.player.closeInventory();
                    e.player.openInventory(AllTrophiesGUI.open(e.player));
                })
                .build();
    }

    public static ItemStack setName(Trophy trophy) {
        return new ButtonCreator.ButtonBuilder(new ItemStack(Material.PAPER))
                .name("§6Set name")
                .lore("§bCurrently set to: §c" + trophy.getDisplayName())
                .onClick("setName", e -> {
                    e.player.closeInventory();
                    ChatInputRegistry.waitFor(
                            e.player,
                            List.of("name"),
                            input -> {
                                trophy.setDisplayName(ChatColor.translateAlternateColorCodes('&', input));
                                trophy.markDirty();
                                e.player.sendMessage(Lang.get("trophy.nameSet", Map.of("name", ChatColor.translateAlternateColorCodes('&', input))));
                                Bukkit.getScheduler().runTask(Trophies.getInstance(), () -> e.player.openInventory(TrophyGUI.open(trophy)));
                            });
                })
                .build();
    }

    public static ItemStack setSlot(Trophy trophy) {
        return new ButtonCreator.ButtonBuilder(new ItemStack(Material.NAME_TAG))
                .name("§3Set slot")
                .lore("§bCurrently set to: §c" + trophy.getSlot())
                .onClick("setSlot", e -> {
                    e.player.closeInventory();
                    ChatInputRegistry.waitFor(
                            e.player,
                            List.of("slot"),
                            input -> {
                                try {
                                    int slot = Integer.parseInt(input);
                                    if (TrophyManager.checkSlot(slot)) {
                                        e.player.sendMessage(Lang.prefix() + Lang.get("trophy.slot-invalid", Map.of("slot", ChatColor.translateAlternateColorCodes('&', input))));
                                    } else {
                                        trophy.setSlot(slot);
                                        e.player.sendMessage(Lang.prefix() + Lang.get("trophy.slot-set", Map.of("input", ChatColor.translateAlternateColorCodes('&', input))));
                                        TrophyManager.saveTrophy(trophy);
                                    }
                                } catch (NumberFormatException ex) {
                                    e.player.sendMessage("Number not valid");
                                    e.player.openInventory(TrophyGUI.open(trophy));
                                }
                                Bukkit.getScheduler().runTask(Trophies.getInstance(), () -> e.player.openInventory(TrophyGUI.open(trophy)));
                            });
                })
                .build();
    }

    public static ItemStack setMaterial(Trophy trophy) {
        return new ButtonCreator.ButtonBuilder(trophy.getMaterial())
                .name("§6Set Material")
                .lore(List.of(new String[]{"§7Click with an item on your cursor", "§7to use it as the trophy icon"}))
                .onClick("set-material-" + trophy.getUUID(), e -> {
                    ItemStack cursor = e.event.getCursor();
                    if (cursor == null || cursor.getType().isAir()) {
                        e.player.sendMessage("§cHold an item to set as material.");
                        return;
                    }

                    ItemStack raw = cursor.clone();
                    ItemMeta meta = raw.getItemMeta();
                    if(meta.getLore().contains("§7Click with an item on your cursor")) {
                        meta.setLore(null);
                    }
                    raw.setItemMeta(meta);
                    trophy.setItem(raw);

                    String serialized;
                    try {
                        serialized = ItemSerialization.itemStackToBase64(raw);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                    ConfigManager.getConfig().set("trophies." + trophy.getUUID() + ".item", serialized);


                    TrophyManager.saveTrophy(trophy);

                    e.player.sendMessage("§aMaterial updated to §f" + cursor.getType().name());

                    Bukkit.getScheduler().runTask(Trophies.getInstance(), () -> e.player.openInventory(TrophyGUI.open(trophy)));
                })
                .build();
    }

    public static ItemStack goBack() {
        return new ButtonCreator.ButtonBuilder(new ItemStack(Material.BLAZE_ROD))
                .name("§dGo back")
                .onClick("goBack", e -> {
                    e.player.closeInventory();
                    e.player.openInventory(AdminGUI.open());
                })
                .build();
    }

    public static ItemStack deleteTrophy(Trophy trophy) {
        return new ButtonCreator.ButtonBuilder(new ItemStack(Material.BARRIER))
                .name(Lang.get("buttons.delete.name"))
                .lore(Lang.get("buttons.delete.lore"))
                .onClick("delete-" + trophy.getUUID(), e -> {
                    EventListener.finalizingTrophies.add(e.player.getUniqueId());
                    TrophyManager.deleteTrophy(trophy);
                    e.player.sendMessage(Lang.get("trophy.deleted"));
                    e.player.closeInventory();
                })
                .build();
    }
}
