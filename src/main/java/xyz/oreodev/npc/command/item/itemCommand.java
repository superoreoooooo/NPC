package xyz.oreodev.npc.command.item;

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
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("remove")) {
                    se.removeItem(player.getItemInHand());
                    if (player.getItemInHand().hasItemMeta()) {
                        player.sendMessage("item removed : " + player.getItemInHand().getItemMeta().getDisplayName());
                    } else {
                        player.sendMessage("item removed : " + player.getItemInHand().getType().name());
                    }
                }
                if (args[0].equalsIgnoreCase("list")) {
                    se.printList(player);
                }
            }
            else if (args.length == 2) {
                if (!args[0].equalsIgnoreCase("set")) return false;
                se.setItem(player.getItemInHand(), Integer.parseInt(args[1]));
                player.sendMessage("item added : " + player.getItemInHand().getItemMeta().getDisplayName() + " price : " + plugin.priceDataYmlManager.getConfig().get("item." + player.getItemInHand().getType().toString() + ".name." + player.getItemInHand().getItemMeta().getDisplayName() + ".price"));
            }
            else {
                player.sendMessage("/item set (price) | remove");
            }
        }
        return false;
    }
}