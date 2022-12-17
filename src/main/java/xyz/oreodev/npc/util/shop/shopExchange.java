package xyz.oreodev.npc.util.shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.oreodev.npc.Main;
import xyz.oreodev.npc.util.acc.account;

import java.util.HashMap;

public class shopExchange {
    private account acc;
    private Main plugin;

    public shopExchange() {
        this.acc = new account();
        this.plugin = JavaPlugin.getPlugin(Main.class);
    }

    public void printList(Player player) {
        for (String itemType : plugin.priceDataYmlManager.getConfig().getConfigurationSection("item.").getKeys(false)) {
            for (String itemName : plugin.priceDataYmlManager.getConfig().getConfigurationSection("item." + itemType + ".name.").getKeys(false)) {
                player.sendMessage("Name : " + itemName + " | Type : " + itemType + " | Price : " + plugin.priceDataYmlManager.getConfig().getInt("item." + itemType + ".name." + itemName + ".price"));
            }
        }
    }

    public void setItem(ItemStack itemStack, int price) {
        plugin.priceDataYmlManager.getConfig().set("item." + itemStack.getType().name() + ".name." + itemStack.getItemMeta().getDisplayName() + ".price", price);
        plugin.priceDataYmlManager.saveConfig();
    }

    public void removeItem(ItemStack itemStack) {
        for (String itemType : plugin.priceDataYmlManager.getConfig().getConfigurationSection("item.").getKeys(false)) {
            if (itemType.equals(itemStack.getType().name())) {
                for (String itemName : plugin.priceDataYmlManager.getConfig().getConfigurationSection("item." + itemType + ".name.").getKeys(false)) {
                    if (!itemStack.hasItemMeta()) { // no custom name
                        plugin.priceDataYmlManager.getConfig().set("item." + itemType + ".name." + itemName, null);
                        plugin.priceDataYmlManager.saveConfig();
                        return;
                    }
                    else if (itemName.equals(itemStack.getItemMeta().getDisplayName())) {
                        plugin.priceDataYmlManager.getConfig().set("item." + itemType + ".name." + itemName, null);
                        plugin.priceDataYmlManager.saveConfig();
                    }
                }
            }
        }
    }

    public void sellItem(Player player, ItemStack itemStack) {
        for (String itemType : plugin.priceDataYmlManager.getConfig().getConfigurationSection("item.").getKeys(false)) {
            if (itemType.equals(itemStack.getType().name())) {
                for (String itemName : plugin.priceDataYmlManager.getConfig().getConfigurationSection("item." + itemType + ".name.").getKeys(false)) {
                    if (!itemStack.getItemMeta().hasDisplayName()) {
                        int price = plugin.priceDataYmlManager.getConfig().getInt("item." + itemType + ".name." + itemName + ".price");
                        acc.addBalance(player.getName(), price);
                        player.sendMessage("sold item : " + itemStack.getType().name() + " for " + price);
                        player.sendMessage("balance left : " + acc.getBalance(player.getName()));
                        itemStack.setAmount(itemStack.getAmount() - 1);
                        return;
                    }
                    else if (itemName.equals(itemStack.getItemMeta().getDisplayName())) {
                        int price = plugin.priceDataYmlManager.getConfig().getInt("item." + itemType + ".name." + itemName + ".price");
                        acc.addBalance(player.getName(), price);
                        player.sendMessage("sold item : " + itemStack.getItemMeta().getDisplayName() + " for " + price);
                        player.sendMessage("balance left : " + acc.getBalance(player.getName()));
                        itemStack.setAmount(itemStack.getAmount() - 1);
                    }
                }
            }
        }
    }

    public void buyItem(Player player, ItemStack itemStack) {
        for (String itemType : plugin.priceDataYmlManager.getConfig().getConfigurationSection("item.").getKeys(false)) {
            if (itemType.equals(itemStack.getType().name())) {
                for (String itemName : plugin.priceDataYmlManager.getConfig().getConfigurationSection("item." + itemType + ".name.").getKeys(false)) {
                    if (!itemStack.getItemMeta().hasDisplayName()){
                        int price = plugin.priceDataYmlManager.getConfig().getInt("item." + itemType + ".name." + itemName + ".price");
                        if (!acc.addBalance(player.getName(), -1 * price)) {
                            player.sendMessage("you have no money :( (balance left : " + acc.getBalance(player.getName()) + ")");
                            return;
                        }
                        player.sendMessage("bought item : " + itemStack.getType().name() + " price : " + price);
                        player.sendMessage("balance left : " + acc.getBalance(player.getName()));
                        ItemStack clone = itemStack.clone();
                        clone.setAmount(1);
                        player.getInventory().addItem(clone);
                        return;
                    } else if (itemName.equals(itemStack.getItemMeta().getDisplayName())) {
                        int price = plugin.priceDataYmlManager.getConfig().getInt("item." + itemType + ".name." + itemName + ".price");
                        if (!acc.addBalance(player.getName(), -1 * price)) {
                            player.sendMessage("you have no money :( (balance left : " + acc.getBalance(player.getName()) + ")");
                            return;
                        }
                        player.sendMessage("bought item : " + itemStack.getItemMeta().getDisplayName() + " price : " + price);
                        player.sendMessage("balance left : " + acc.getBalance(player.getName()));
                        ItemStack clone = itemStack.clone();
                        clone.setAmount(1);
                        player.getInventory().addItem(clone);
                    }
                }
            }
        }
    }
}
