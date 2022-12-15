package xyz.oreodev.npc.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.oreodev.npc.Main;
import xyz.oreodev.npc.util.shop.shopExchange;

public class itemCommand implements CommandExecutor {
    private shopExchange se;
    private Main plugin;

    public itemCommand() {
        this.se = new shopExchange();
        this.plugin = JavaPlugin.getPlugin(Main.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player player) {
            if (player.getItemInHand() == null) return false;
            if (args.length == 2) {
                if (!args[0].equalsIgnoreCase("add")) return false;
                se.addItem(player.getItemInHand(), Integer.parseInt(args[1]));
                player.sendMessage("item added : " + player.getItemInHand().getItemMeta().getDisplayName() + " price : " + plugin.priceDataYmlManager.getConfig().get("item." + player.getItemInHand().getType().toString() + ".name." + player.getItemInHand().getItemMeta().getDisplayName() + ".price"));
            }
            else {
                player.sendMessage("/i");
            }
        }
        return false;
    }
}
