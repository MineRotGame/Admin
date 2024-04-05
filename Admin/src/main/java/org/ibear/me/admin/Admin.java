package org.ibear.me.admin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationOptions;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Admin extends JavaPlugin implements Listener {
    public static Inventory inv;
    public ItemStack current, clear;
    public ItemMeta im1;
    public ChatColor CC;
    public UUID uuid;
    private HashMap<UUID, Long> cooldown = new HashMap<>();
    private Long lastSec, now, leftSec;

    // INT
    private int intTime = 300; // 5 Minutes

    private File configFile;
    private FileConfiguration config;


    // READ COOLDOWN PLAYERS
    private void loadCooldowns() {
        if (config.contains("cooldowns")) {
            for (String uuidString : config.getConfigurationSection("cooldowns").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidString);
                long lastCooldown = config.getLong("cooldowns." + uuidString);
                cooldown.put(uuid, lastCooldown);
            }
        }
    }

    // SAVE COOLDOWN PLAYERS
    private void saveCooldowns() {
        for (Map.Entry<UUID, Long> entry : cooldown.entrySet()) {
            config.set("cooldowns." + entry.getKey().toString(), entry.getValue());
        }
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getServer().getLogger().info("v1.0");

        // สร้างหรือโหลด config
        configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }
    // show


    // Create Menu
    public void CreateMenu(Player p) {
        // Gui
        inv = Bukkit.getServer().createInventory(null, 9, "Admin");


        // Item 1
        clear = new ItemStack(Material.BARRIER, 1);
        im1 = clear.getItemMeta();
        im1.setDisplayName(CC.GREEN + "CLEAR");
        im1.setLore(Arrays.asList(CC.WHITE + "CLEAR YOUR INVENTORY"));
        clear.setItemMeta(im1);
        inv.setItem(0, clear);

        p.openInventory(inv);
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (command.getName().equalsIgnoreCase("ad")) {
                CreateMenu(p);
            }
        }
        return true;
    }

    @EventHandler
    public void onInventoryDragEvent(InventoryDragEvent e) {
        if (e.getInventory().equals(inv)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryChickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        current = e.getCurrentItem();
        uuid = p.getUniqueId();
        now = System.currentTimeMillis();

        if (current == null || !e.getInventory().equals(inv)) return;

        e.setCancelled(true);
        if (current.getType() == Material.BARRIER) {
            // cooldown
            if (!cooldown.containsKey(uuid) || now - cooldown.get(uuid) > intTime * 1000) {
                // กระทำเมื่ออยู่นอก cooldown
                p.sendMessage(CC.GREEN + "CLEAR DONE!");
                p.chat("/clear");
                p.closeInventory();
                cooldown.put(uuid, now);
            } else {
                leftSec = (intTime - (now - cooldown.get(uuid)) / 1000);
                p.sendMessage("Please wait for cooldown! Time left: " + leftSec + " seconds");
            }
        }
    }

    @Override
    public void onDisable() {
        saveCooldowns();
    }
}
