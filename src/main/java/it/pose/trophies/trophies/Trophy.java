package it.pose.trophies.trophies;

import it.pose.trophies.ItemSerialization;
import it.pose.trophies.Trophies;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class Trophy implements ConfigurationSerializable {
    private UUID uuid;
    private String name;
    private String displayName;
    private ItemStack item;
    private List<String> lore;
    private Integer slot;
    private transient boolean dirty = false;
    private static final Trophies main = Trophies.getInstance();

    public Trophy() {
        this.uuid = UUID.randomUUID();
        this.name = "Unnamed Trophy";
        this.item = new ItemStack(Material.PAPER);
        this.lore = new ArrayList<>();
        this.slot = -1;
    }

    public Trophy(UUID id, String name, String displayName, ItemStack item, List<String> lore, int slot) {
        this.uuid = id;
        this.name = name;
        this.displayName = displayName;
        this.item = item;
        this.lore = lore;
        this.slot = slot;
    }


    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("uuid", uuid.toString());
        map.put("name", name);
        map.put("displayName", displayName);

        // *** branch here: if it’s a player head, Base64-serialize ***
        if (item.getType() == Material.PLAYER_HEAD) {
            try {
                String b64 = ItemSerialization.itemStackToBase64(item);
                map.put("itemBase64", b64);
            } catch (Exception ex) {
                throw new IllegalStateException("Unable to serialize trophy head", ex);
            }
        } else {
            map.put("item", item);
        }

        map.put("slot", slot);
        map.put("lore", lore);
        return map;
    }

    @SuppressWarnings("unchecked")
    public static Trophy deserialize(Map<String, Object> map) {
        UUID uuid = UUID.fromString((String) map.get("uuid"));
        String name = (String) map.get("name");
        String displayName = (String) map.getOrDefault("displayName", name);

        ItemStack item;
        if (map.containsKey("itemBase64")) {
            try {
                item = ItemSerialization.itemStackFromBase64((String) map.get("itemBase64"));
            } catch (Exception ex) {
                throw new IllegalStateException("Unable to deserialize trophy head", ex);
            }
        } else {
            item = (ItemStack) map.get("item");
        }

        int slot = (Integer) map.get("slot");
        List<String> lore = (List<String>) map.getOrDefault("lore", new ArrayList<>());
        return new Trophy(uuid, name, displayName, item, lore, slot);
    }

    public static Trophy fromItemStack(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;

        ItemMeta meta = item.getItemMeta();
        String displayName = meta.hasDisplayName() ? ChatColor.stripColor(meta.getDisplayName()) : "Unnamed Trophy";
        String name = displayName.toLowerCase().replaceAll("[^a-z0-9_]", "_");
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

        Trophy trophy = new Trophy();

        NamespacedKey key = new NamespacedKey(Trophies.getInstance(), "trophy-uuid");
        if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            UUID id = UUID.fromString(meta.getPersistentDataContainer().get(key, PersistentDataType.STRING));
            trophy.setUUID(id);
        } else {
            trophy.setUUID(UUID.randomUUID());
        }

        trophy.setUUID(UUID.randomUUID());
        trophy.setName(name);
        trophy.setDisplayName(displayName);
        trophy.setItem(item);
        trophy.setLore(lore);
        trophy.setSlot(-1);

        return trophy;
    }

    public ItemStack toItemStack() {
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        meta.setLore(this.lore);

        if (lore != null && !lore.isEmpty()) {
            meta.setLore(lore.stream()
                    .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                    .toList());
        }

        meta.addEnchant(Enchantment.LOYALTY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(meta);
        return item;
    }

    public void setName(String name) {
        if (!this.name.equals(name)) {
            this.name = name;
            this.displayName = name;
            markDirty();
        }
    }

    public void setItem(ItemStack item) {
        if (!Objects.equals(this.item, item)) {
            this.item = item.clone(); // clone to prevent reference issues
            markDirty();
        }
    }

    public void setSlot(int slot) {
        if (this.slot != slot) {
            this.slot = slot;
            markDirty();
        }
    }

    public void setLore(List<String> lore) {
        if (!this.lore.equals(lore)) {
            this.lore = new ArrayList<>(lore);
            markDirty();
        }
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
        markDirty();
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        markDirty();
    }

    public void markDirty() {
        this.dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void clearDirtyFlag() {
        this.dirty = false;
    }

    public ItemStack createItem() {
        ItemMeta meta = item.getItemMeta();

        // Store UUID in persistent data
        NamespacedKey key = new NamespacedKey(main, "trophy-uuid");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, uuid.toString());

        meta.setDisplayName(displayName);
        meta.setLore(lore);

        meta.addEnchant(Enchantment.LOYALTY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(meta);
        return item;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public ItemStack getMaterial() {
        return item;
    }

    public List<String> getLore() {
        return new ArrayList<>(lore);
    }

    public Integer getSlot() {
        return slot;
    }

    public String getDisplayName() {
        return displayName;
    }

}
