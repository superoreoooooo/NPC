package xyz.oreodev.npc.util.shop;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.oreodev.npc.Main;

import java.util.UUID;

public class shopInventory implements InventoryHolder {
    private Inventory inv;
    private UUID InventoryID;
    private final Main plugin;
    private shopUtil util;
    private String title;
    private int size;

    public shopInventory(UUID InventoryID, String title, int size) {
        this.InventoryID = InventoryID;
        this.title = title;
        this.size = size;
        this.plugin = JavaPlugin.getPlugin(Main.class);
        this.util = new shopUtil();
        this.init();
    }

    private void init() {
        inv = Bukkit.createInventory(this, getSize(), getTitle() + "_상점");
        for (int i = 0; i < getSize(); i++) {
            inv.setItem(i, plugin.shopYmlManager.getConfig().getItemStack("shop." + InventoryID.toString() + ".inventory." + i));
        }
        saveInventory();
    }

    public void saveInventory() {
        util.saveInventorySize(InventoryID, getSize());
        util.saveTitle(InventoryID, getTitle());
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public UUID getInventoryID() {
        return InventoryID;
    }

    public void setInventoryID(UUID inventoryID) {
        InventoryID = inventoryID;
    }
}