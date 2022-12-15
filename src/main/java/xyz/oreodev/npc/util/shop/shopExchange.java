package xyz.oreodev.npc.util.shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.oreodev.npc.Main;
import xyz.oreodev.npc.util.acc.account;

public class shopExchange {
    private account acc;
    private Main plugin;

    public shopExchange() {
        this.acc = new account();
        this.plugin = JavaPlugin.getPlugin(Main.class);
    }

    public void addItem(ItemStack itemStack, int price) {
        plugin.priceDataYmlManager.getConfig().set("item." + itemStack.getType().name() + ".name." + itemStack.getItemMeta().getDisplayName() + ".price", price);
        plugin.priceDataYmlManager.saveConfig();
    }

    public void sellItem(Player player, ItemStack itemStack) {
        for (String itemType : plugin.priceDataYmlManager.getConfig().getConfigurationSection("item.").getKeys(false)) {
            if (itemType.equals(itemStack.getType().name())) {
                for (String itemName : plugin.priceDataYmlManager.getConfig().getConfigurationSection("item." + itemType + ".name.").getKeys(false)) {
                    if (!itemStack.hasItemMeta()) {
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
                    if (!itemStack.hasItemMeta()) {
                        int price = plugin.priceDataYmlManager.getConfig().getInt("item." + itemType + ".name." + itemName + ".price");
                        if (!acc.addBalance(player.getName(), -1 * price)) {
                            player.sendMessage("you have no money :( (balance left : " + acc.getBalance(player.getName()) + ")");
                            return;
                        }
                        player.sendMessage("bought item : " + itemStack.getType().name() + " for " + price);
                        player.sendMessage("balance left : " + acc.getBalance(player.getName()));
                        ItemStack clone = itemStack.clone();
                        clone.setAmount(1);
                        player.getInventory().addItem(clone);
                        return;
                    }
                    else if (itemName.equals(itemStack.getItemMeta().getDisplayName())) {
                        int price = plugin.priceDataYmlManager.getConfig().getInt("item." + itemType + ".name." + itemName + ".price");
                        if (!acc.addBalance(player.getName(), -1 * price)) {
                            player.sendMessage("you have no money :( (balance left : " + acc.getBalance(player.getName()) + ")");
                            return;
                        }
                        player.sendMessage("sold item : " + itemStack.getItemMeta().getDisplayName() + " price : " + price);
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
