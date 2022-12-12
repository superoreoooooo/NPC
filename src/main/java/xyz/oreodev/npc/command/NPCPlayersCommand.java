package xyz.oreodev.npc.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.oreodev.npc.Main;
import xyz.oreodev.npc.NPCPlayer;
import xyz.oreodev.npc.util.Color;

import java.util.ArrayList;
import java.util.List;

public class NPCPlayersCommand implements CommandExecutor {
    private NPCPlayer npcPlayer;

    public NPCPlayersCommand() {
        this.npcPlayer = new NPCPlayer();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("administrators")) {
            if (args.length == 0) {
                sender.sendMessage("");
                sender.sendMessage(Color.format("&2> &3/npc add (Name) &b- summons a fake player"));
                sender.sendMessage(Color.format("&2> &3/npc add (Name) (Number) &b- summons a certain amount of fake players"));
                sender.sendMessage(Color.format("&2> &3/npc remove (Name/All) &b- disbands fake players"));
                sender.sendMessage(Color.format("&2> &3/npc list &b- displays a fake player list"));
                sender.sendMessage(Color.format("&2> &3/npc reload &b- reloads config.yml"));
                sender.sendMessage("");
            } else {
                switch (args[0]) {
                    case "save":
                        npcPlayer.saveNPCPlayer();
                        break;
                    case "load":
                        npcPlayer.loadNPCPlayers();
                        break;
                    case "add":
                        if (args.length == 2) {
                            summon(sender, args[1]);
                        } else if (args.length == 5) {
                            summon(sender, args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
                        }
                        else {
                            Bukkit.dispatchCommand(sender, "npc");
                        }
                        break;
                    case "remove":
                        if (args.length == 2) {
                            disband(sender, args[1]);
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
                    case "reload":
                        Bukkit.getPluginManager().disablePlugin(Main.getPlugin());
                        Bukkit.getPluginManager().getPlugin("FakePlayers").reloadConfig();
                        Bukkit.getPluginManager().enablePlugin(Main.getPlugin());
                        sender.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.reload"));
                        break;
                    default:
                        Bukkit.dispatchCommand(sender, "npc");
                        break;
                }
            }
        }
        else {
            sender.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.no-permissions"));
        }
        return false;
    }

    public void summon(CommandSender sender, String name, double x, double y, double z) {
        if (NPCPlayer.summon(name, x, y, z, Main.getRandomUUID(name))) {
            sender.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.summon.success-one"));
        } else {
            sender.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.summon.failed"));
        }
    }

    public void summon(CommandSender sender, String name) {
        Location loc = ((Player)sender).getLocation();
        if (NPCPlayer.summon(name, loc.getX(), loc.getY(), loc.getZ(), Main.getRandomUUID(name))) {
            sender.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.summon.success-one"));
        } else {
            sender.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.summon.failed"));
        }
    }

    private void disband(CommandSender sender, String name) {
        if (name.equalsIgnoreCase("All")) {
            List<NPCPlayer> copy = new ArrayList<>(NPCPlayer.getNPCPlayerList());
            for (NPCPlayer player : copy) {
                player.removeDataAndPlayer();
            }
            sender.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.disband.all-disbanded-success"));
        } else {
            NPCPlayer npePlayer = NPCPlayer.getNPCPlayer(name);

            if (npePlayer != null) {
                npePlayer.removeDataAndPlayer();
                sender.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.disband.one-disbanded-success"));
            } else {
                sender.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.disband.failed"));
            }
        }
    }

    private void list(CommandSender sender) {
        sender.sendMessage(Color.format("&aNpcs (" + NPCPlayer.getAmount() + "):"));

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