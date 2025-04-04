package it.pose.trophies;

import de.tr7zw.nbtapi.NBT;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class Trophy {

    private String id;
    private String name;
    private ItemStack item;
    private Integer slot;
    private String lore;


    public Trophy(Integer slot, ItemStack item, String displayName) {

    }

    private ItemStack processItem(ItemStack baseItem, String displayName) {
        ItemStack cloned = baseItem.clone();
        ItemMeta meta = cloned.getItemMeta();

        Trophies.getInstance().getLogger().warning(meta.toString());

        if (displayName != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "§r§f" + displayName));
        }

        meta.addEnchant(Enchantment.MENDING, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        cloned.setItemMeta(meta);

        NBT.modify(cloned, nbt -> {
            nbt.setInteger("trophyId", slot);
        });

        return cloned;
    }

    public ItemStack item() {
        return item.clone();
    }


    public Object serialize() {
        return Map.of(
                "item", item
        );
    }

    public static Trophy deserialize(Integer slot, Map<String, Object> data) {
        return new Trophy(
                slot,
                (ItemStack) data.get("item"),
                (String) data.get("item.display-name")
        );
    }

    public Integer getSlot(){
        return slot;
    }

}