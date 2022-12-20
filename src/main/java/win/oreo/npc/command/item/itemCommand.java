package win.oreo.npc.command.item;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import win.oreo.npc.Main;
import win.oreo.npc.util.item.itemUtil;

public class itemCommand implements CommandExecutor {
    private itemUtil util;

    public itemCommand() {
        this.util = new itemUtil();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player player) {
            if (checkPermission(sender)) {
                sender.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.no-permission", args));
                return false;
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("remove")) {
                    if (player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) return false;
                    util.removePrice(player.getInventory().getItemInMainHand());
                    String[] strings = new String[1];
                    strings[0] = itemUtil.getItemName(player.getInventory().getItemInMainHand());
                    player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.item.remove", strings));
                }
                if (args[0].equalsIgnoreCase("list")) {
                    util.printList(player);
                }
                if (args[0].equalsIgnoreCase("test")) {
                    util.testItem(player);
                }
                if (args[0].equalsIgnoreCase("save")) {
                    util.savePriceData();
                }
            }
            else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("set")) {
                    if (player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) return false;
                    util.setPrice(player.getInventory().getItemInMainHand(), Integer.parseInt(args[1]));
                    String[] strings = new String[2];
                    strings[0] = itemUtil.getItemName(player.getInventory().getItemInMainHand());
                    strings[1] = args[1];
                    player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.item.set", strings));
                }
                if (args[0].equalsIgnoreCase("give")) {
                    player.getInventory().addItem(util.getItemStack(args[1]));
                }
            }
            else {
                player.sendMessage("wrong command");
            }
        }
        return false;
    }

    public boolean checkPermission(CommandSender sender) {
        return !sender.hasPermission("administrators");
    }
}
