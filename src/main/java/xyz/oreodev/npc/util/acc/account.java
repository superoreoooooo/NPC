package xyz.oreodev.npc.util.acc;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.oreodev.npc.Main;

import java.util.HashMap;
import java.util.Set;

public class account {
    private Main plugin;
    public static HashMap<String, Integer> accountMap = new HashMap<>();

    public account() {
        this.plugin = JavaPlugin.getPlugin(Main.class);
    }

    public int getBalance(String playerName) {
        if (plugin.moneyConfig.getConfig().get("account." + playerName + ".balance") != null) return plugin.moneyConfig.getConfig().getInt("account." + playerName + ".balance");
        else return 0;
    }

    public Set<String> getAccountList() {
        return plugin.moneyConfig.getConfig().getConfigurationSection("account.").getKeys(false);
    }

    public void removeAccount(String playerName) {
        plugin.moneyConfig.getConfig().set("account." + playerName, null);
        plugin.moneyConfig.saveConfig();
        accountMap.remove(playerName);
    }

    public void setBalance(String playerName, int balanceToSet) {
        plugin.moneyConfig.getConfig().set("account." + playerName + ".balance", balanceToSet);
        plugin.moneyConfig.saveConfig();
        accountMap.remove(playerName);
        accountMap.put(playerName, balanceToSet);
    }

    public boolean addBalance(String playerName, int balanceToAdd) {
        if (plugin.moneyConfig.getConfig().get("account." + playerName + ".balance") != null) {
            if (getBalance(playerName) + balanceToAdd < 0) {
                return false;
            }
            setBalance(playerName, getBalance(playerName) + balanceToAdd);
            accountMap.remove(playerName);
            accountMap.put(playerName, getBalance(playerName) + balanceToAdd);
        } else {
            setBalance(playerName, balanceToAdd);
            accountMap.remove(playerName);
            accountMap.put(playerName, balanceToAdd);
        }
        return true;
    }

    public void printAllAccountData(CommandSender sender) {
        sender.sendMessage("========================================");
        for (String name : accountMap.keySet()) {
            sender.sendMessage("account : " + name + " | balance : " + getBalance(name));
        }
        sender.sendMessage("========================================");
    }
}
