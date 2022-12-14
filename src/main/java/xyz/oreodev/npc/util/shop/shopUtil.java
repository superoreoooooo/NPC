package xyz.oreodev.npc.util.shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.oreodev.npc.Main;
import xyz.oreodev.npc.command.NPCCommand;

import java.util.HashMap;
import java.util.UUID;

public class shopUtil {
    private Main plugin;
    public static HashMap<UUID, String> shopMap = new HashMap<>(); //ID, Name

    public shopUtil() {
        this.plugin = JavaPlugin.getPlugin(Main.class);
    }


    public ItemStack getSavedItemStack(UUID InventoryID, int placeholder) {
        return plugin.shopYmlManager.getConfig().getItemStack("shop." + InventoryID.toString() + ".inventory." + placeholder);
    }

    public void saveItemStack(UUID InventoryID, int placeholder, ItemStack itemStack) {
        plugin.shopYmlManager.getConfig().set("shop." + InventoryID.toString() + ".inventory." + placeholder, itemStack);
        plugin.shopYmlManager.saveConfig();
    }

    public int getSavedInventorySize(UUID InventoryID) {
        return plugin.shopYmlManager.getConfig().getInt("shop." + InventoryID.toString() + ".size");
    }

    public void saveInventorySize(UUID InventoryID, int size) {
        plugin.shopYmlManager.getConfig().set("shop." + InventoryID.toString() + ".size", size);
        plugin.shopYmlManager.saveConfig();
    }

    public String getSavedTitle(UUID InventoryID) {
        return plugin.shopYmlManager.getConfig().getString("shop." + InventoryID.toString() + ".title");
    }

    public void saveTitle(UUID InventoryID, String title) {
        plugin.shopYmlManager.getConfig().set("shop." + InventoryID.toString() + ".title", title);
        plugin.shopYmlManager.saveConfig();
    }

    public void removeInventory(UUID InventoryID) {
        plugin.shopYmlManager.getConfig().set("shop." + InventoryID.toString(), null);
        plugin.shopYmlManager.saveConfig();
    }

    public UUID getIDFromName(String name) {
        for (UUID key : shopUtil.shopMap.keySet()) {
            if (shopUtil.shopMap.get(key).equalsIgnoreCase(name)) {
                return key;
            }
        }
        return null;
    }

    public void openShop(Player player, String name) {
        if (!shopMap.containsValue(name)) {
            return;
        }
        UUID key = getIDFromName(name);
        shopInventory shopInventory = new shopInventory(key, getSavedTitle(key), getSavedInventorySize(key));
        player.openInventory(shopInventory.getInventory());
    }

    public void editShop(Player player, String name) {
        if (!shopMap.containsValue(name)) {
            player.sendMessage("/shop edit (name)");
            return;
        }
        NPCCommand.editorList.add(player);
        UUID key = getIDFromName(name);
        shopInventory shopInventory = new shopInventory(key, getSavedTitle(key), getSavedInventorySize(key));
        player.sendMessage("UUID  > " + key);
        player.sendMessage("Title > " + getSavedTitle(key));
        player.sendMessage("Size  > " + getSavedInventorySize(key));
        for (int i = 0; i < getSavedInventorySize(key); i++) {
            if (getSavedItemStack(key, i) == null) continue;
            player.sendMessage("Item" + i + "  > " + getSavedItemStack(key, i));
        }
        player.openInventory(shopInventory.getInventory());
    }

    public void removeShop(Player player, String name) {
        if (!shopMap.containsValue(name)) {
            player.sendMessage("/shop remove (name)");
            return;
        }
        UUID key = getIDFromName(name);
        player.sendMessage("UUID  > " + key.toString());
        player.sendMessage("Title > " + getSavedTitle(key));
        player.sendMessage("Size  > " + getSavedInventorySize(key));
        for (int i = 0; i < getSavedInventorySize(key); i++) {
            player.sendMessage("Item" + i + "  > " + getSavedItemStack(key, i));
        }
        removeInventory(key);
        shopUtil.shopMap.remove(key);
    }

    public void createShop(Player player, String name, String size) {
        if (shopMap.containsValue(name)) {
            player.sendMessage("overlapping names are impossible");
            return;
        }
        if (Integer.parseInt(size) % 9 != 0) {
            player.sendMessage("chest size must be multiples of 9");
            return;
        }
        shopInventory shopInventory = new shopInventory(UUID.randomUUID(), name, Integer.parseInt(size));
        player.sendMessage("new Shop Created");
        player.sendMessage("UUID  > " + shopInventory.getInventoryID());
        player.sendMessage("Title > " + shopInventory.getTitle());
        player.sendMessage("Size  > " + shopInventory.getSize());
        player.openInventory(shopInventory.getInventory());
        shopMap.put(shopInventory.getInventoryID(), shopInventory.getTitle());
        NPCCommand.editorList.add(player);
    }
}
