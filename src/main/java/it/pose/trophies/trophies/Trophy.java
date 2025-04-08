package it.pose.trophies.trophies;

import it.pose.trophies.Trophies;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class Trophy {
    private UUID uuid;
    private String name;
    private Material material;
    private List<String> lore;
    private Integer slot;
    private transient boolean dirty = false;
    private static final Trophies main = Trophies.getInstance();

    public Trophy() {
        this.uuid = UUID.randomUUID();
        this.name = "Unnamed Trophy";
        this.material = Material.PAPER;
        this.lore = new ArrayList<>();
        
    }

    // Serialization
    public Map<String, Object> serialize() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("uuid", uuid.toString());
        data.put("name", name);
        data.put("material", material.name());
        data.put("slot", slot);
        data.put("lore", lore);
        return data;
    }

    // Deserialization
    public static Trophy deserialize(Map<String, Object> data) {
        Trophy trophy = new Trophy();
        trophy.uuid = UUID.fromString((String) data.get("uuid"));
        trophy.name = (String) data.get("name");
        trophy.material = Material.valueOf((String) data.get("material"));
        trophy.lore = (List<String>) data.get("lore");
        return trophy;
    }

    // Property Setters (mark as dirty when modified)
    public Trophy setName(String name) {
        if (!this.name.equals(name)) {
            this.name = name;
            markDirty();
        }
        return this;
    }

    public Trophy setMaterial(Material material) {
        if (this.material != material) {
            this.material = material;
            markDirty();
        }
        return this;
    }

    public Trophy setSlot(int slot) {
        if (this.slot != slot) {
            this.slot = slot;
            markDirty();
        }
        return this;
    }

    public Trophy setLore(List<String> lore) {
        if (!this.lore.equals(lore)) {
            this.lore = new ArrayList<>(lore);
            markDirty();
        }
        return this;
    }

    public Trophy addLoreLine(String line) {
        if (lore.add(line)) {
            markDirty();
        }
        return this;
    }

    private void markDirty() {
        this.dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void clearDirtyFlag() {
        this.dirty = false;
    }

    // Item generation with persistent UUID
    public ItemStack createItem() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        // Store UUID in persistent data
        NamespacedKey key = new NamespacedKey(main, "trophy-uuid");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, uuid.toString());

        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    // Getters
    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public List<String> getLore() {
        return new ArrayList<>(lore);
    }

    public Integer getSlot() {
        return slot;
    }

}
