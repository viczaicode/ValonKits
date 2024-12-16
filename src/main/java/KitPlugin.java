package main.java;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public  class KitPlugin  extends JavaPlugin {

    private FileConfiguration config;
    private Map<String, Kit> kits = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();
        loadKitsFromConfig();
        getLogger().info("ValonKits bekapcsolva!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ValonKits kikapcsolva!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Ezt a parancsot csak játékosok használhatják.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Használat: /kit <kit>");
            return true;
        }

        String kitName = args[0];

        boolean kitFound = false;
        for (String availableKit : kits.keySet()) {
            if (availableKit.equalsIgnoreCase(kitName)) {
                kitFound = true;
                giveKit(player, kits.get(availableKit));
                player.sendMessage(ChatColor.GREEN + "Megkaptad ezt a kitet: " + availableKit);
                break;
            }
        }
        if (!kitFound) {
            player.sendMessage(ChatColor.RED + "Ilyen kit nem található: " + kitName);
        }
        return true;
    }


    private void giveKit(Player player, Kit kit) {
        player.getInventory().clear();
        kit.getItems().forEach((slot, item) -> player.getInventory().setItem(slot, item));
        player.getInventory().setHelmet(kit.getHelmet());
        player.getInventory().setChestplate(kit.getChestplate());
        player.getInventory().setLeggings(kit.getLeggings());
        player.getInventory().setBoots(kit.getBoots());

        // Apply potion effects
        kit.getEffects().forEach(effect -> player.addPotionEffect(effect));
    }

    private void loadKitsFromConfig() {
        kits.clear();
        if (!config.contains("kitek")) return;

        for (String kitName : config.getConfigurationSection("kitek").getKeys(false)) {
            String path = "kitek." + kitName + ".";

            // Load armor
            ItemStack helmet = loadItemFromConfig(path + "szett.sisak");
            ItemStack chestplate = loadItemFromConfig(path + "szett.mellvert");
            ItemStack leggings = loadItemFromConfig(path + "szett.nadrag");
            ItemStack boots = loadItemFromConfig(path + "szett.csizma");

            // Load items
            Map<Integer, ItemStack> items = new HashMap<>();
            if (config.contains(path + "itemek")) {
                for (String slotStr : config.getConfigurationSection(path + "itemek").getKeys(false)) {
                    int slot = Integer.parseInt(slotStr);
                    items.put(slot, loadItemFromConfig(path + "itemek." + slotStr));
                }
            }

            // Load effects
            List<PotionEffect> effects = loadEffectsFromConfig(path + "effektek");

            kits.put(kitName, new Kit(kitName, helmet, chestplate, leggings, boots, items, effects));
        }
    }

    private ItemStack loadItemFromConfig(String path) {
        if (!config.contains(path)) return null;

        Material material = Material.getMaterial(config.getString(path + ".targy"));
        if (material == null) return null;


        int amount = config.getInt(path + ".mennyiseg", 1);

        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();

        if (config.contains(path + ".nev")) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString(path + ".nev")));
        }

        if (config.contains(path + ".encsantok")) {
            for (String enchantStr : config.getConfigurationSection(path + ".encsantok").getKeys(false)) {
                Enchantment enchantment = Enchantment.getByName(enchantStr);
                int level = config.getInt(path + ".encsantok." + enchantStr);
                if (enchantment != null) {
                    meta.addEnchant(enchantment, level, true);
                }
            }
        }

        item.setItemMeta(meta);
        return item;
    }

    private List<PotionEffect> loadEffectsFromConfig(String path) {
        if (!config.contains(path)) return Collections.emptyList();

        return config.getMapList(path).stream().map(effectMap -> {
            Map<String, Object> map = (Map<String, Object>) effectMap;

            String typeStr = (String) map.get("type");
            if (typeStr == null) return null;

            PotionEffectType type = PotionEffectType.getByName(typeStr.toUpperCase());
            if (type == null) return null;

            int duration = map.containsKey("duration") ? ((Number) map.get("duration")).intValue() : 200;
            int amplifier = map.containsKey("amplifier") ? ((Number) map.get("amplifier")).intValue() : 0;

            return new PotionEffect(type, duration, amplifier);
        }).filter(effect -> effect != null).collect(Collectors.toList());
    }


    class Kit {
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
}
