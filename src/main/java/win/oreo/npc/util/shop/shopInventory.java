package win.oreo.npc.util.shop;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import win.oreo.npc.Main;
import win.oreo.npc.util.item.itemUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class shopInventory implements InventoryHolder {
    private Inventory inv;
    private UUID InventoryID;
    private final Main plugin;
    private shopUtil util;
    private itemUtil itemUtil;
    private String title;
    private int size;

    public shopInventory(UUID InventoryID, String title, int size) {
        this.InventoryID = InventoryID;
        this.title = title;
        this.size = size;
        this.plugin = JavaPlugin.getPlugin(Main.class);
        this.util = new shopUtil();
        this.itemUtil = new itemUtil();
        this.init();
    }

    private void init() {
        inv = Bukkit.createInventory(this, getSize(), getTitle() + "_상점");
        for (int i = 0; i < getSize(); i++) {
            ItemStack itemStack = util.getSavedItemStack(InventoryID, i);
            if (itemStack != null) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta.hasLore()) {
                    for (String str : itemMeta.getLore()) {
                        if (str.contains("원")) {
                            itemMeta.getLore().remove(0);
                        }
                    }
                }
                List<String> lore = new ArrayList<>();
                lore.add(itemUtil.getPriceByName(win.oreo.npc.util.item.itemUtil.getItemName(itemStack)) + "원");
                if (itemMeta.hasLore()) {
                    for (String str : itemMeta.getLore()) {
                        if (!str.contains("원")) lore.add(str);
                    }
                }
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);
            }
            inv.setItem(i, itemStack);
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