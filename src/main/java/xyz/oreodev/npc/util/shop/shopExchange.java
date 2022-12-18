package xyz.oreodev.npc.util.shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.oreodev.npc.Main;
import xyz.oreodev.npc.util.account.account;

public class shopExchange {
    private account acc;
    private Main plugin;

    public shopExchange() {
        this.acc = new account();
        this.plugin = JavaPlugin.getPlugin(Main.class);
    }

    public void printList(Player player) {
        String[] args = new String[3];
        for (String itemType : plugin.priceDataYmlManager.getConfig().getConfigurationSection("item.").getKeys(false)) {
            for (String itemName : plugin.priceDataYmlManager.getConfig().getConfigurationSection("item." + itemType + ".name.").getKeys(false)) {
                args[0] = itemName;
                args[1] = itemType;
                args[2] = String.valueOf(plugin.priceDataYmlManager.getConfig().getInt("item." + itemType + ".name." + itemName + ".price"));
                player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.exchange.list", args));
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
                    if (!itemStack.hasItemMeta()) {
                        plugin.priceDataYmlManager.getConfig().set("item." + itemType + ".name." + itemName, null);
                        plugin.priceDataYmlManager.saveConfig();
                        return;
                    }
                    else if (!itemStack.getItemMeta().hasDisplayName()) {
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
        String[] args = new String[2];
        for (String itemType : plugin.priceDataYmlManager.getConfig().getConfigurationSection("item.").getKeys(false)) {
            if (itemType.equals(itemStack.getType().name())) {
                for (String itemName : plugin.priceDataYmlManager.getConfig().getConfigurationSection("item." + itemType + ".name.").getKeys(false)) {
                    if (!itemStack.getItemMeta().hasDisplayName()) {
                        int price = plugin.priceDataYmlManager.getConfig().getInt("item." + itemType + ".name." + itemName + ".price");
                        acc.addBalance(player.getName(), price);
                        args[0] = itemStack.getType().name();
                        args[1] = String.valueOf(price);
                        player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.exchange.sell", args));
                        itemStack.setAmount(itemStack.getAmount() - 1);
                        return;
                    }
                    else if (itemName.equals(itemStack.getItemMeta().getDisplayName())) {
                        int price = plugin.priceDataYmlManager.getConfig().getInt("item." + itemType + ".name." + itemName + ".price");
                        acc.addBalance(player.getName(), price);
                        args[0] = itemStack.getItemMeta().getDisplayName();
                        args[1] = String.valueOf(price);
                        player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.exchange.sell", args));
                        itemStack.setAmount(itemStack.getAmount() - 1);
                    }
                }
            }
        }
    }

    public void buyItem(Player player, ItemStack itemStack) {
        String[] args = new String[2];
        for (String itemType : plugin.priceDataYmlManager.getConfig().getConfigurationSection("item.").getKeys(false)) {
            if (itemType.equals(itemStack.getType().name())) {
                for (String itemName : plugin.priceDataYmlManager.getConfig().getConfigurationSection("item." + itemType + ".name.").getKeys(false)) {
                    if (!itemStack.getItemMeta().hasDisplayName()){
                        int price = plugin.priceDataYmlManager.getConfig().getInt("item." + itemType + ".name." + itemName + ".price");
                        if (!acc.addBalance(player.getName(), -1 * price)) {
                            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.account.no-balance", args));
                            return;
                        }
                        args[0] = itemStack.getType().name();
                        args[1] = String.valueOf(price);
                        player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.exchange.buy", args));
                        ItemStack clone = itemStack.clone();
                        clone.setAmount(1);
                        ItemMeta cloneMeta = clone.getItemMeta();
                        cloneMeta.setLore(null);
                        clone.setItemMeta(cloneMeta);
                        player.getInventory().addItem(clone);
                        return;
                    } else if (itemName.equals(itemStack.getItemMeta().getDisplayName())) {
                        int price = plugin.priceDataYmlManager.getConfig().getInt("item." + itemType + ".name." + itemName + ".price");
                        if (!acc.addBalance(player.getName(), -1 * price)) {
                            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.account.no-balance", args));
                            return;
                        }
                        args[0] = itemStack.getItemMeta().getDisplayName();
                        args[1] = String.valueOf(price);
                        player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.exchange.buy", args));
                        ItemStack clone = itemStack.clone();
                        clone.setAmount(1);
                        ItemMeta cloneMeta = clone.getItemMeta();
                        cloneMeta.setLore(null);
                        clone.setItemMeta(cloneMeta);
                        player.getInventory().addItem(clone);
                    }
                }
            }
        }
    }
}
