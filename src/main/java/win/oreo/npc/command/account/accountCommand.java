package win.oreo.npc.command.account;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import win.oreo.npc.Main;
import win.oreo.npc.util.account.account;

public class accountCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            account account = new account();
            switch (args[0]) {
                case "help" :
                    sender.sendMessage("/account now | list | help | set (targetPlayer) (amount) | add (targetPlayer) (amount) | send (targetPlayer) (amount) | remove (targetPlayer)");
                    break;
                case "now" :
                    if (sender instanceof Player player) {
                        String[] list = {sender.getName(), String.valueOf(account.getBalance(sender.getName()))};
                        player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.account.balance-now", list));
                    } else {
                        sender.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.no-player", args));
                    }
                    break;
                case "send" :
                    if (args.length == 3) {
                        if (sender instanceof Player player) {
                            if (!account.getAccountList().contains(args[1])) {
                                player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.account.wrong-name", args));
                                return false;
                            }
                            if (account.getBalance(player.getName()) - Integer.parseInt(args[2]) < 0) {
                                player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.account.no-balance", args));
                                return false;
                            }
                            account.addBalance(player.getName(), -1 * Integer.parseInt(args[2]));
                            account.addBalance(args[1], Integer.parseInt(args[2]));
                            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.account.send", args));
                        } else {
                            sender.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.no-player", args));
                        }
                    }
                    break;
                case "remove" :
                    if (checkPermission(sender)) {
                        sender.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.no-permission", args));
                        return false;
                    }
                    if (args.length == 2) {
                        sender.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.account.remove", args));
                        account.removeAccount(args[1]);
                    }
                    else Bukkit.dispatchCommand(sender, "account help");
                    break;
                case "list" :
                    if (checkPermission(sender)) {
                        sender.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.no-permission", args));
                        return false;
                    }
                    account.printAllAccountData(sender);
                    break;
                case "set" :
                    if (checkPermission(sender)) {
                        sender.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.no-permission", args));
                        return false;
                    }
                    if (args.length == 3) {
                        account.setBalance(args[1], Integer.parseInt(args[2]));
                        sender.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.account.set", args));
                    }
                    else Bukkit.dispatchCommand(sender, "account help");
                    break;
                case "add" :
                    if (checkPermission(sender)) {
                        sender.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.no-permission", args));
                        return false;
                    }
                    if (args.length == 3) {
                        if (account.addBalance(args[1], Integer.parseInt(args[2]))) {
                            sender.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.account.add", args));
                        } else {
                            sender.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.account.wrong-name", args));
                        }
                    }
                    else Bukkit.dispatchCommand(sender, "account help");
                    break;
            }
        } else {
            Bukkit.dispatchCommand(sender, "account help");
        }
        return false;
    }

    public boolean checkPermission(CommandSender sender) {
        return !sender.hasPermission("administrators");
    }
}
