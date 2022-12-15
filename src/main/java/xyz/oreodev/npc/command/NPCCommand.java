package xyz.oreodev.npc.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.oreodev.npc.Main;
import xyz.oreodev.npc.util.npc.NPCPlayer;
import xyz.oreodev.npc.util.npc.Color;
import xyz.oreodev.npc.util.shop.shopUtil;

import java.util.ArrayList;
import java.util.List;

public class NPCCommand implements CommandExecutor {
    private NPCPlayer npcPlayer;
    private shopUtil util;
    private Main plugin;

    public static List<Player> editorList = new ArrayList<>();

    public NPCCommand() {
        this.npcPlayer = new NPCPlayer();
        this.util = new shopUtil();
        this.plugin = JavaPlugin.getPlugin(Main.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        if (sender.hasPermission("administrators")) {
            if (args.length == 0) {
                sender.sendMessage("/npc list | open (name) | add (name) (size) | remove (name) / (all) | edit (name) | size (name)");
            } else {
                switch (args[0]) {
                    case "save":
                        plugin.saveNPC();
                        break;
                    case "add":
                        if (args.length == 2) {
                            summon(sender, args[1]);
                            util.createShop((Player) sender, args[1], "9");
                        } else if (args.length == 3) {
                            summon(sender, args[1]);
                            util.createShop((Player) sender, args[1], args[2]);
                        }
                        else {
                            Bukkit.dispatchCommand(sender, "npc");
                        }
                        break;
                    case "size":
                        if (args.length == 3) {
                            util.saveInventorySize(util.getIDFromName(args[1]), Integer.parseInt(args[2]));
                        } else {
                            Bukkit.dispatchCommand(sender, "npc");
                        }
                        break;
                    case "remove":
                        if (args.length == 2) {
                            remove(sender, args[1]);
                            util.removeShop((Player) sender, args[1]);
                        } else {
                            Bukkit.dispatchCommand(sender, "npc");
                        }
                        break;
                    case "list":
                        if (args.length == 1) {
                            list(sender);
                        } else {
                            Bukkit.dispatchCommand(sender, "npc");
                        }
                        break;
                    case "open":
                        if (args.length == 2) {
                            util.openShop((Player) sender, args[1]);
                        }
                        break;
                    case "edit":
                        if (args.length == 2) {
                            util.editShop((Player) sender, args[1]);
                        }
                        break;
                    default:
                        Bukkit.dispatchCommand(sender, "npc");
                        break;
                }
            }
        }
        else {
            sender.sendMessage("no permissions!");
        }
        return false;
    }

    public void summon(CommandSender sender, String name) {
        Location loc = ((Player)sender).getLocation();
        if (NPCPlayer.summon(name, loc.getX(), loc.getY(), loc.getZ(), Main.getRandomUUID(name))) {
            sender.sendMessage("succeed to add new npc!");
        } else {
            sender.sendMessage("failed to add new npc!");
        }
    }

    private void remove(CommandSender sender, String name) {
        if (name.equalsIgnoreCase("All")) {
            List<NPCPlayer> copy = new ArrayList<>(NPCPlayer.getNPCPlayerList());
            for (NPCPlayer player : copy) {
                player.removeDataAndPlayer();
            }
            sender.sendMessage("removed all npc!");
        } else {
            NPCPlayer npePlayer = NPCPlayer.getNPCPlayer(name);

            if (npePlayer != null) {
                npePlayer.removeDataAndPlayer();
                sender.sendMessage("removed npc : " + npcPlayer.getName());
            } else {
                sender.sendMessage("failed to remove npc : " + npcPlayer.getName());
            }
        }
    }

    private void list(CommandSender sender) {
        sender.sendMessage("Npc (" + NPCPlayer.getAmount() + "):");

        StringBuilder list = new StringBuilder();

        for (NPCPlayer player : NPCPlayer.getNPCPlayerList()) {
            list.append(Color.format(player.getName() + ", "));
        }

        if (list.length() >= 3) {
            list = new StringBuilder(list.substring(0, list.length() - 2));

            String[] lists = new String[(int) Math.ceil((double) list.length() / 48.0d)];

            int cuts = 0;
            int lastCut = 0;
            StringBuilder listToAdd = new StringBuilder();
            for (int i = 0; i < list.length(); i++) {
                char c = list.charAt(i);

                if (c == ',' && i - lastCut >= 48) {
                    lastCut = i;
                    lists[cuts] = listToAdd.toString();
                    cuts++;
                    listToAdd = new StringBuilder();
                    i++;
                    continue;
                }
                listToAdd.append(c);
            }

            if (!listToAdd.toString().equals("")) {
                lists[cuts] = listToAdd.toString();
            }

            for (String s : lists) {
                sender.sendMessage(s);
            }
        }
    }
}