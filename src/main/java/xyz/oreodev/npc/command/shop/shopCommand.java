package xyz.oreodev.npc.command.shop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.oreodev.npc.util.shop.shopUtil;

import java.util.*;

public class shopCommand implements CommandExecutor {
    private shopUtil util;

    public static List<Player> editorList = new ArrayList<>();

    public shopCommand() {
        this.util = new shopUtil();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        util.initialize();
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            if (args.length == 0) {
                player.sendMessage("/shop list | open (name) | remove (name) | edit (name) | create (name) (size)");
            }
            else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("list")) {
                    util.listShop(player);
                }
                else if (args[0].equalsIgnoreCase("item")) {
                    util.giveItem(player);
                }
                else {
                    player.sendMessage("/shop list | open (name) | remove (name) | edit (name) | create (name) (size)");
                }
            }
            else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("open")) {
                    util.openShop(player, args[1]);
                }
                else if (args[0].equalsIgnoreCase("edit")) {
                    util.editShop(player, args[1]);
                }
                else if (args[0].equalsIgnoreCase("remove")) {
                    util.removeShop(player, args[1]);
                } else {
                    player.sendMessage("/shop list | open (name) | remove (name) | edit (name) | create (name) (size)");
                }
            }
            else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("c")) {
                    util.createShop(player, args[1], args[2]);
                }
                else {
                    player.sendMessage("/shop list | open (name) | remove (name) | edit (name) | create (name) (size)");
                }
            }
            else {
                player.sendMessage("/shop list | open (name) | remove (name) | edit (name) | create (name) (size)");
            }
        }
        return false;
    }
}