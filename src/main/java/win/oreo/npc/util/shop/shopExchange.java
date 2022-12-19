package win.oreo.npc.util.shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import win.oreo.npc.Main;
import win.oreo.npc.util.account.account;

public class shopExchange {
    private account acc;
    private Main plugin;

    public shopExchange() {
        this.acc = new account();
        this.plugin = JavaPlugin.getPlugin(Main.class);
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
