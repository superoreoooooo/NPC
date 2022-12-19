package win.oreo.npc.util.shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import win.oreo.npc.Main;
import win.oreo.npc.util.account.account;
import win.oreo.npc.util.item.itemUtil;


public class shopExchange {
    private account acc;
    private Main plugin;
    private itemUtil util;

    public shopExchange() {
        this.acc = new account();
        this.util = new itemUtil();
        this.plugin = JavaPlugin.getPlugin(Main.class);
    }
    
    public void sell(Player player, ItemStack itemStack) {
        String name = itemUtil.getItemName(itemStack);
        String[] args = new String[2];

        for (ItemStack item : itemUtil.priceMap.keySet()) {
            if (itemUtil.getItemName(item).equals(name) && item.getType().equals(itemStack.getType())) {
                int price = util.getPrice(item);
                args[0] = itemUtil.getItemName(item);
                args[1] = String.valueOf(price);

                if (item.getAmount() > itemStack.getAmount()) {
                    player.sendMessage("개수 부족");
                    return;
                }
                itemStack.setAmount(itemStack.getAmount() - item.getAmount());
                acc.addBalance(player.getName(), price);
                player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.exchange.sell", args));
            }
        }

        /**
        if (itemUtil.priceMap.containsKey(itemStack)) {
            int price = util.getPrice(itemStack);
            args[0] = itemUtil.getItemName(itemStack);
            args[1] = String.valueOf(price);

            itemStack.setAmount(0);
            acc.addBalance(player.getName(), price);
            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.exchange.sell", args));
        }**/
    }
    
    public void buy(Player player, ItemStack itemStack) {
        String name = itemUtil.getItemName(itemStack);
        String[] priceLore = itemStack.getItemMeta().getLore().get(0).split("원");
        String[] args = new String[2];
        int price = Integer.parseInt(priceLore[0]);

        for (ItemStack item : itemUtil.priceMap.keySet()) {
            if (itemUtil.getItemName(item).equals(name) && item.getType().equals(itemStack.getType()) && util.getPrice(item) == price) {
                args[0] = itemUtil.getItemName(item);
                args[1] = String.valueOf(price);
                if (!acc.addBalance(player.getName(), -1 * price)) {
                    player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.account.no-balance", args));
                    return;
                }
                player.getInventory().addItem(item);
                player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.exchange.buy", args));
            }
        }
    }

    public void sellItem(Player player, ItemStack itemStack) {
        String[] args = new String[2];
        for (String itemType : plugin.priceDataYmlManager.getConfig().getConfigurationSection("item.").getKeys(false)) {
            if (itemType.equals(itemStack.getType().name())) {
                for (String itemName : plugin.priceDataYmlManager.getConfig().getConfigurationSection("item." + itemType + ".name.").getKeys(false)) {
                    args[0] = itemStack.getType().name();
                    if (!itemStack.hasItemMeta()) {
                        int price = plugin.priceDataYmlManager.getConfig().getInt("item." + itemType + ".name." + itemName + ".price");
                        args[0] = itemStack.getType().name();
                        args[1] = String.valueOf(price);
                        ItemStack itemStack1 = plugin.priceDataYmlManager.getConfig().getItemStack("item." + itemType + ".name." + itemName + ".itemStack");
                        if (itemStack1.getAmount() > itemStack.getAmount()) {
                            player.sendMessage("개수 부족");
                            return;
                        }
                        itemStack.setAmount(itemStack.getAmount() - itemStack1.getAmount());
                        acc.addBalance(player.getName(), price);
                        player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.exchange.sell", args));
                        return;
                    }
                    else if (!itemStack.getItemMeta().hasDisplayName()) {
                        int price = plugin.priceDataYmlManager.getConfig().getInt("item." + itemType + ".name." + itemName + ".price");
                        args[0] = itemStack.getType().name();
                        args[1] = String.valueOf(price);
                        ItemStack itemStack1 = plugin.priceDataYmlManager.getConfig().getItemStack("item." + itemType + ".name." + itemName + ".itemStack");
                        if (itemStack1.getAmount() > itemStack.getAmount()) {
                            player.sendMessage("개수 부족");
                            return;
                        }
                        itemStack.setAmount(itemStack.getAmount() - itemStack1.getAmount());
                        acc.addBalance(player.getName(), price);
                        player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.exchange.sell", args));
                        return;
                    }
                    else if (itemName.equals(itemStack.getItemMeta().getDisplayName())) {
                        int price = plugin.priceDataYmlManager.getConfig().getInt("item." + itemType + ".name." + itemName + ".price");
                        args[0] = itemStack.getItemMeta().getDisplayName();
                        args[1] = String.valueOf(price);
                        ItemStack itemStack1 = plugin.priceDataYmlManager.getConfig().getItemStack("item." + itemType + ".name." + itemName + ".itemStack");
                        if (itemStack1.getAmount() > itemStack.getAmount()) {
                            player.sendMessage("개수 부족");
                            return;
                        }
                        itemStack.setAmount(itemStack.getAmount() - itemStack1.getAmount());
                        acc.addBalance(player.getName(), price);
                        player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.exchange.sell", args));
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
                    player.sendMessage(itemName);
                    if (!itemStack.getItemMeta().hasDisplayName()){
                        int price = plugin.priceDataYmlManager.getConfig().getInt("item." + itemType + ".name." + itemName + ".price");
                        if (!acc.addBalance(player.getName(), -1 * price)) {
                            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.account.no-balance", args));
                            return;
                        }
                        args[0] = itemStack.getType().name();
                        args[1] = String.valueOf(price);
                        player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.exchange.buy", args));
                        player.getInventory().addItem(plugin.priceDataYmlManager.getConfig().getItemStack("item." + itemType + ".name." + itemName + ".itemStack"));
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
                        player.getInventory().addItem(plugin.priceDataYmlManager.getConfig().getItemStack("item." + itemType + ".name." + itemName + ".itemStack"));
                    }
                }
            }
        }
    }
}
