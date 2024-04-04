package org.ibear.me.admin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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

import java.util.Arrays;
import java.util.HashMap;
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

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getServer().getLogger().info("v1.0");

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

        if (current == null) return;
        if (e.getInventory().equals(inv)) {
            e.setCancelled(true);
            if (current.getType() == Material.BARRIER) {
                if (cooldown.containsKey(uuid) && (System.currentTimeMillis() - cooldown.get(uuid)) <= 1000) {
    p.sendMessage("กูก็งงค่าอิดอกทอง อีเหรี้ย");
} else {
    lastSec = cooldown.get(uuid);
    now = System.currentTimeMillis();
    if (lastSec != null && now - lastSec < intTime) {
        p.sendMessage("§cPLEASE WAITING: " + (now - lastSec));
        return;
    }
    p.sendMessage(CC.GREEN + "CLEAR DONE!");
    p.chat("/clear");
    p.closeInventory();
    cooldown.put(uuid, now);
                }
            }
        }
    }
}
