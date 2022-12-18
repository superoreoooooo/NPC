package win.oreo.npc.command.item;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import win.oreo.npc.Main;
import win.oreo.npc.util.shop.shopExchange;

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
                    String[] strings = new String[1];
                    if (player.getItemInHand().hasItemMeta()) {
                        strings[0] = player.getItemInHand().getItemMeta().getDisplayName();
                    } else {
                        strings[0] = player.getItemInHand().getType().name();
                    }
                    player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.item.remove", strings));
                }
                if (args[0].equalsIgnoreCase("list")) {
                    se.printList(player);
                }
                if (args[0].equalsIgnoreCase("g")) {
                    se.exitem(player);
                }
            }
            else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("set")) {
                    String[] strings = new String[2];
                    if (plugin.priceDataYmlManager.getConfig().getConfigurationSection("item." + player.getItemInHand().getType().name() + ".name") != null && plugin.priceDataYmlManager.getConfig().getConfigurationSection("item." + player.getItemInHand().getType().name() + ".name").getKeys(false).contains(player.getItemInHand().getItemMeta().getDisplayName())) {
                        if (player.getItemInHand().hasItemMeta()) {
                            strings[0] = player.getItemInHand().getItemMeta().getDisplayName();
                        } else {
                            strings[0] = player.getItemInHand().getType().name();
                        }
                        player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.item.error-overlap", strings));
                        return false;
                    }
                    se.setItem(player.getItemInHand(), Integer.parseInt(args[1]));

                    if (player.getItemInHand().hasItemMeta()) {
                        strings[0] = player.getItemInHand().getItemMeta().getDisplayName();
                    } else {
                        strings[0] = player.getItemInHand().getType().name();
                    }
                    strings[1] = args[1];
                    player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.item.set", strings));
                }
            }
            else {
                player.sendMessage("/item set (price) | remove");
            }
        }
        return false;
    }
}
