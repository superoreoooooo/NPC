package win.oreo.npc.util.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
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
        List<String> names = new ArrayList<>();
        priceMap.keySet().forEach(s -> names.add(getItemName(s)));
        for (String itemName : names) {
            if (getItemName(itemStack).equalsIgnoreCase(itemName)) {
                priceMap.remove(getItemStack(itemName));
                plugin.priceDataYmlManager.getConfig().set("item." + itemName, null);
                plugin.priceDataYmlManager.saveConfig();
            }
        }

        plugin.priceDataYmlManager.getConfig().set("item." + getItemName(itemStack) + ".itemStack", itemStack);
        plugin.priceDataYmlManager.getConfig().set("item." + getItemName(itemStack) + ".price", price);
        plugin.priceDataYmlManager.saveConfig();

        priceMap.put(itemStack, price);
    }

    public int getPriceByName(String name) {
        return getPrice(getItemStack(name));
    }

    public int getPrice(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().equals(Material.AIR) || priceMap.get(itemStack) == null) return 0;
        return priceMap.get(itemStack);
    }

    public void removePrice(ItemStack itemStack) {
        if (priceMap.containsKey(itemStack)) {
            plugin.priceDataYmlManager.getConfig().set("item." + getItemName(itemStack), null);
            plugin.priceDataYmlManager.saveConfig();

            priceMap.remove(itemStack);
        }
    }

    public void testItem(Player player) {
        ItemStack item = new ItemStack(Material.DIAMOND_AXE);
        item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1000000);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GOLD + "세계관최강자");
        lore.add("");
        meta.setLore(lore);
        meta.setDisplayName("지존도끼");
        item.setItemMeta(meta);
        player.getInventory().addItem(item);
    }

    public ItemStack getItemStack(String name) {
        return plugin.priceDataYmlManager.getConfig().getItemStack("item." + name + ".itemStack");
    }

    public void printList(Player player) {
        String[] args = new String[4];
        for (String name : plugin.priceDataYmlManager.getConfig().getConfigurationSection("item.").getKeys(false)) {
            if (name.equals(Material.AIR.name())) continue;
            args[0] = name;
            args[1] = getItemStack(name).getType().name();
            args[2] = String.valueOf(getPrice(getItemStack(name)));
            args[3] = String.valueOf(getItemStack(name).getAmount());
            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.item.list", args));
        }
    }
}
