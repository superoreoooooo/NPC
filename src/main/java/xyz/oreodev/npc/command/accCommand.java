package xyz.oreodev.npc.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.oreodev.npc.util.acc.account;

public class accCommand implements CommandExecutor {
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
                        player.sendMessage("your balance : " + account.getBalance(player.getName()));
                    } else {
                        sender.sendMessage("player only");
                    }
                    break;
                case "send" :
                    if (args.length == 3) {
                        if (sender instanceof Player player) {
                            if (!account.getAccountList().contains(args[1])) {
                                player.sendMessage("check player name");
                                return false;
                            }
                            if (!account.addBalance(player.getName(), account.getBalance(player.getName()) - Integer.parseInt(args[2]))) {
                                player.sendMessage("you have no balance :( (" + account.getBalance(player.getName()) + ")");
                                return false;
                            }
                            account.addBalance(player.getName(), -1 * Integer.parseInt(args[2]));
                            account.addBalance(args[1], Integer.parseInt(args[2]));
                            player.sendMessage("money sent to " + args[1] + " (" + args[2] + ")");
                        } else {
                            sender.sendMessage("player only");
                        }
                    }
                    break;
                case "remove" :
                    if (!checkPermission(sender)) {
                        sender.sendMessage("no permission!");
                        return false;
                    }
                    if (args.length == 2) {
                        sender.sendMessage("removed account : " + args[1] + " | amount : " + account.getBalance(args[1]));
                        account.removeAccount(args[1]);
                    }
                    else Bukkit.dispatchCommand(sender, "account help");
                    break;
                case "list" :
                    if (!checkPermission(sender)) {
                        sender.sendMessage("no permission!");
                        return false;
                    }
                    account.printAllAccountData(sender);
                    break;
                case "set" :
                    if (!checkPermission(sender)) {
                        sender.sendMessage("no permission!");
                        return false;
                    }
                    if (args.length == 3) {
                        account.setBalance(args[1], Integer.parseInt(args[2]));
                        sender.sendMessage("money set to " + args[1] + " | amount : " + Integer.parseInt(args[2]));
                    }
                    else Bukkit.dispatchCommand(sender, "account help");
                    break;
                case "add" :
                    if (!checkPermission(sender)) {
                        sender.sendMessage("no permission!");
                        return false;
                    }
                    if (args.length == 3) {
                        if (account.addBalance(args[1], Integer.parseInt(args[2]))) {
                            sender.sendMessage("money add to " + args[1] + " | amount : " + Integer.parseInt(args[2]) + " | total : " + account.getBalance(args[1]));
                        } else {
                            sender.sendMessage("error");
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
        return sender.hasPermission("administrators");
    }
}
