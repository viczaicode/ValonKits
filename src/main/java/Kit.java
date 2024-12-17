package main.java;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Map;

public class Kit {
    private final String name;
    private final ItemStack helmet;
    private final ItemStack chestplate;
    private final ItemStack leggings;
    private final ItemStack boots;
    private final Map<Integer, ItemStack> items;
    private final List<PotionEffect> effects;

    public Kit(String name, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, Map<Integer, ItemStack> items, List<PotionEffect> effects) {
        this.name = name;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.items = items;
        this.effects = effects;
    }

    public String getName() {
        return name;
    }

    public ItemStack getHelmet() {
        return helmet;
    }

    public ItemStack getChestplate() {
        return chestplate;
    }

    public ItemStack getLeggings() {
        return leggings;
    }

    public ItemStack getBoots() {
        return boots;
    }

    public Map<Integer, ItemStack> getItems() {
        return items;
    }

    public List<PotionEffect> getEffects() {
        return effects;
    }
}
