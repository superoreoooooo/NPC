package xyz.oreodev.npc.command.npc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.oreodev.npc.Main;
import xyz.oreodev.npc.listener.shop.shopListener;
import xyz.oreodev.npc.util.npc.NPCPlayer;
import xyz.oreodev.npc.util.npc.Color;
import xyz.oreodev.npc.util.shop.shopUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.no-player", args));
            return false;
        }
        if (sender.hasPermission("administrators")) {
            if (args.length == 0) {
                sender.sendMessage("/npc list | open (name) | tp (name) | add (name) (size) | skin (name) (skinOwner) | remove (name) / (all) | edit (name) | size (name)");
            } else {
                switch (args[0]) {
                    case "save" :
                        plugin.saveNPC();
                        player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.npc.save", args));
                        break;
                    case "skin" :
                        if (args.length == 3) {
                            setSkin(player, args[1], args[2]);
                            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.npc.skin", args));
                        }
                        break;
                    case "tp" :
                        if (args.length == 2) {
                            teleport(player, args[1]);
                            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.npc.tp-player", args));
                        }
                        if (args.length == 5) {
                            if (args[3].equals("~")) teleport(player, args[1], Double.parseDouble(args[2]), player.getLocation().getY(), Double.parseDouble(args[4]));
                            else teleport(player, args[1], Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]));
                            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.npc.tp-location", args));
                        }
                        break;
                    case "add" :
                        if (args.length == 2) {
                            summon(player, args[1]);
                            util.createShop(player, args[1], "9");
                            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.npc.add-default", args));
                        } else if (args.length == 3) {
                            summon(player, args[1]);
                            util.createShop(player, args[1], args[2]);
                            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.npc.add-modified", args));
                        }
                        else {
                            Bukkit.dispatchCommand(player, "npc");
                        }
                        break;
                    case "size" :
                        if (args.length == 3) {
                            if (Integer.parseInt(args[2]) % 9 != 0) return false;
                            util.saveInventorySize(util.getIDFromName(args[1]), Integer.parseInt(args[2]));
                            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.npc.size", args));
                        } else {
                            Bukkit.dispatchCommand(player, "npc");
                        }
                        break;
                    case "remove" :
                        if (args.length == 2) {
                            remove(player, args[1]);
                            util.removeShop(player, args[1]);
                            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.npc.remove", args));
                        } else {
                            Bukkit.dispatchCommand(player, "npc");
                        }
                        break;
                    case "list" :
                        if (args.length == 1) {
                            list(player);
                        } else {
                            Bukkit.dispatchCommand(sender, "npc");
                        }
                        break;
                    case "open" :
                        if (args.length == 2) {
                            util.openShop(player, args[1]);
                            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.npc.open", args));
                        }
                        break;
                    case "edit" :
                        if (args.length == 2) {
                            util.editShop(player, args[1]);
                            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.npc.edit", args));
                        }
                        break;
                    default:
                        Bukkit.dispatchCommand(player, "npc");
                        break;
                }
            }
        }
        else {
            sender.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.no-permission", args));
        }
        return false;
    }

    public void teleport(Player player, String name) {
        teleport(player, name, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
    }

    public void teleport(CommandSender sender, String name, double x, double y, double z) {
        NPCPlayer player = NPCPlayer.getNPCPlayer(name);
        if (player == null) {
            Bukkit.dispatchCommand(sender, "npc");
            return;
        }
        UUID uid = player.getUUID();
        String uuid = uid.toString();
        player.removePlayer();
        plugin.ymlManager.getConfig().set("npc." + uuid + ".locX", x);
        plugin.ymlManager.getConfig().set("npc." + uuid + ".locY", y);
        plugin.ymlManager.getConfig().set("npc." + uuid + ".locZ", z);
        plugin.ymlManager.saveConfig();
        NPCPlayer.summon(plugin.ymlManager.getConfig().getString("npc." + uuid + ".name"), plugin.ymlManager.getConfig().getDouble("npc." + uuid + ".locX"), plugin.ymlManager.getConfig().getDouble("npc." + uuid + ".locY"), plugin.ymlManager.getConfig().getDouble("npc." + uuid + ".locZ"), UUID.fromString(uuid), plugin.ymlManager.getConfig().getString("npc." + uuid + ".skin"));
    }

    public void setSkin(CommandSender sender, String name, String skin) {
        NPCPlayer player = NPCPlayer.getNPCPlayer(name);
        if (player == null) {
            Bukkit.dispatchCommand(sender, "npc");
            return;
        }
        UUID uid = player.getUUID();
        String uuid = uid.toString();
        player.removePlayer();
        plugin.ymlManager.getConfig().set("npc." + uuid + ".skin", skin);
        plugin.ymlManager.saveConfig();
        NPCPlayer.summon(plugin.ymlManager.getConfig().getString("npc." + uuid + ".name"), plugin.ymlManager.getConfig().getDouble("npc." + uuid + ".locX"), plugin.ymlManager.getConfig().getDouble("npc." + uuid + ".locY"), plugin.ymlManager.getConfig().getDouble("npc." + uuid + ".locZ"), UUID.fromString(uuid), plugin.ymlManager.getConfig().getString("npc." + uuid + ".skin"));
    }

    public void summon(CommandSender sender, String name) {
        Location loc = ((Player)sender).getLocation();
        if (NPCPlayer.summon(name, loc.getX(), loc.getY(), loc.getZ(), Main.getRandomUUID(name), name)) {
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
            NPCPlayer npcPlayer = NPCPlayer.getNPCPlayer(name);

            if (npcPlayer != null) {
                npcPlayer.removeDataAndPlayer();
                sender.sendMessage("removed npc : " + this.npcPlayer.getName());
            } else {
                sender.sendMessage("failed to remove npc : " + this.npcPlayer.getName());
            }
        }
    }

    private void list(CommandSender sender) {
        String[] args = new String[1];
        for (NPCPlayer player : NPCPlayer.npcPlayerList) {
            args[0] = player.getName();
            sender.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.npc.list", args));
        }

    }
}