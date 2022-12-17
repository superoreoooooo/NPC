package xyz.oreodev.npc.util.account;

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
        if (plugin.accountYmlManager.getConfig().get("account." + playerName + ".balance") != null) return plugin.accountYmlManager.getConfig().getInt("account." + playerName + ".balance");
        else return 0;
    }

    public Set<String> getAccountList() {
        return plugin.accountYmlManager.getConfig().getConfigurationSection("account.").getKeys(false);
    }

    public void removeAccount(String playerName) {
        plugin.accountYmlManager.getConfig().set("account." + playerName, null);
        plugin.accountYmlManager.saveConfig();
        accountMap.remove(playerName);
    }

    public void setBalance(String playerName, int balanceToSet) {
        plugin.accountYmlManager.getConfig().set("account." + playerName + ".balance", balanceToSet);
        plugin.accountYmlManager.saveConfig();
        accountMap.remove(playerName);
        accountMap.put(playerName, balanceToSet);
    }

    public boolean addBalance(String playerName, int balanceToAdd) {
        if (plugin.accountYmlManager.getConfig().get("account." + playerName + ".balance") != null) {
            if (getBalance(playerName) + balanceToAdd < 0) {
                return false;
            }
            setBalance(playerName, getBalance(playerName) + balanceToAdd);
            accountMap.remove(playerName);
            accountMap.put(playerName, getBalance(playerName) + balanceToAdd);
        } else {
            if (balanceToAdd < 0) {
                return false;
            }
            setBalance(playerName, balanceToAdd);
            accountMap.remove(playerName);
            accountMap.put(playerName, balanceToAdd);
        }
        return true;
    }

    public void printAllAccountData(CommandSender sender) {
        for (String name : accountMap.keySet()) {
            String[] args = new String[2];
            args[0] = name;
            args[1] = String.valueOf(getBalance(name));
            sender.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.account.list", args));
        }
    }
}
