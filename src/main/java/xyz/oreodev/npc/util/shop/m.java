package xyz.oreodev.npc.util.shop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class m {
    public final String bar = "====================================================";
    public final String prefix = "[Shop] ";

    public m(String str) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + prefix + bar);
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + prefix + str);
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + prefix + bar);
    }

    public m(double i) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + prefix + bar);
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + prefix + i);
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + prefix + bar);
    }
}
