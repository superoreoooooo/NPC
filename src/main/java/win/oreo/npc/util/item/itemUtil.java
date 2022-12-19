package win.oreo.npc.util.item;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import win.oreo.npc.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class itemUtil {
    public static HashMap<ItemStack, Integer> priceMap = new HashMap<>();

    private Main plugin;

    public itemUtil() {
        this.plugin = JavaPlugin.getPlugin(Main.class);
    }

    public void savePriceData() {
        for (ItemStack itemStack : priceMap.keySet()) {
            plugin.priceDataYmlManager.getConfig().set("item." + getItemName(itemStack) + ".itemStack", itemStack);
            plugin.priceDataYmlManager.getConfig().set("item." + getItemName(itemStack) + ".price", priceMap.get(itemStack));
            plugin.priceDataYmlManager.saveConfig();
        }
    }

    public static String getItemName(ItemStack itemStack) {
        String a;
        if (itemStack.hasItemMeta()) {
            if (itemStack.getItemMeta().hasDisplayName()) {
                a = itemStack.getItemMeta().getDisplayName();
            } else {
                a = itemStack.getType().name();
            }
        } else {
            a = itemStack.getType().name();
        }
        return a;
    }

    public void setPrice(ItemStack itemStack, int price) {
        priceMap.put(itemStack, price);
        plugin.priceDataYmlManager.getConfig().set("item." + getItemName(itemStack) + ".itemStack", itemStack);
        plugin.priceDataYmlManager.getConfig().set("item." + getItemName(itemStack) + ".price", price);
        plugin.priceDataYmlManager.saveConfig();
    }

    public int getPrice(ItemStack itemStack) {
        return priceMap.get(itemStack);
    }

    public void removePrice(ItemStack itemStack) {
        if (priceMap.containsKey(itemStack)) {
            priceMap.remove(itemStack);
            plugin.priceDataYmlManager.getConfig().set("item." + getItemName(itemStack), null);
            plugin.priceDataYmlManager.saveConfig();
        }
    }

    public void testItem(Player player) {
        ItemStack item = new ItemStack(Material.GOLD_AXE);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GOLD + "테스트");
        lore.add("");
        meta.setLore(lore);
        meta.setDisplayName("테스트");
        meta.addEnchant(Enchantment.SILK_TOUCH, 5, true);
        item.setItemMeta(meta);
        player.getInventory().addItem(item);
    }

    public ItemStack getItemStack(String name) {
        return plugin.priceDataYmlManager.getConfig().getItemStack("item." + name + ".itemStack");
    }

    public void printList(Player player) {
        String[] args = new String[3];
        for (String name : plugin.priceDataYmlManager.getConfig().getConfigurationSection("item.").getKeys(false)) {
            args[0] = name;
            args[1] = getItemName(getItemStack(name));
            args[2] = String.valueOf(getPrice(getItemStack(name)));
            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.item.list", args));
        }
    }
}
