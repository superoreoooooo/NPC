package xyz.oreodev.npc.util.shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.oreodev.npc.Main;
import xyz.oreodev.npc.command.npc.NPCCommand;
import xyz.oreodev.npc.listener.shop.shopListener;

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
            return;
        }
        NPCCommand.editorList.add(player);
        UUID key = getIDFromName(name);
        shopInventory shopInventory = new shopInventory(key, getSavedTitle(key), getSavedInventorySize(key));
        player.openInventory(shopInventory.getInventory());
    }

    public void removeShop(Player player, String name) {
        if (!shopMap.containsValue(name)) {
            return;
        }
        UUID key = getIDFromName(name);
        removeInventory(key);
        shopUtil.shopMap.remove(key);
    }

    public void createShop(Player player, String name, String size) {
        if (shopMap.containsValue(name)) {
            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.npc.overlapping-name", new String[]{}));
            return;
        }
        if (Integer.parseInt(size) % 9 != 0) {
            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.npc.multiple-nine", new String[]{}));
            return;
        }
        shopInventory shopInventory = new shopInventory(UUID.randomUUID(), name, Integer.parseInt(size));
        player.openInventory(shopInventory.getInventory());
        shopMap.put(shopInventory.getInventoryID(), shopInventory.getTitle());
        NPCCommand.editorList.add(player);
    }
}
