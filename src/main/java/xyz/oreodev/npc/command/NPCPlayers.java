package xyz.oreodev.npc.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import xyz.oreodev.npc.NPC;
import xyz.oreodev.npc.NPCPlayer;
import xyz.oreodev.npc.action.ActionWait;
import xyz.oreodev.npc.util.Color;

import java.util.ArrayList;
import java.util.List;

public class NPCPlayers implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("administrators")) {
            if (args.length == 0) {
                sender.sendMessage("");
                sender.sendMessage(Color.format("&2[&aFake players - &bHelp (Page 1/1)&2]"));
                sender.sendMessage("");
                sender.sendMessage(Color.format("&2> &3/FakePlayers summon (Name) &b- summons a fake player"));
                sender.sendMessage(Color.format("&2> &3/FakePlayers summon (Name) (Number) &b- summons a certain amount of fake players"));
                sender.sendMessage(Color.format("&2> &3/FakePlayers disband (Name/All) &b- disbands fake players"));
                sender.sendMessage(Color.format("&2> &3/FakePlayers chat (Name/All) (Message/Command) &b- makes fake players type a chat message"));
                sender.sendMessage(Color.format("&2> &3/FakePlayers action (Name/All) (Action) &b- makes fake players add a certain action to their list"));
                sender.sendMessage(Color.format("&2> &3/FakePlayers action (Name/All) perform &b- makes fake players perform all actions from their list"));
                sender.sendMessage(Color.format("&2> &3/FakePlayers action (Name/All) perform (Number) &b- makes fake players perform all actions from their list a certain amount of times"));
                sender.sendMessage(Color.format("&2> &3/FakePlayers macro save (Fake Player's Name) (Macro's Name) &b- creates a macro file from fake player's action list"));
                sender.sendMessage(Color.format("&2> &3/FakePlayers macro load (Fake Player's Name) (Macro's Name) &b- loads macro into fake player's action list"));
                sender.sendMessage(Color.format("&2> &3/FakePlayers macro perform (Fake Player's Name) (Macro's Name) &b- makes fake player perform actions from a macro"));
                sender.sendMessage(Color.format("&2> &3/FakePlayers macro perform (Fake Player's Name) (Macro's Name) (Amount) &b- makes fake player perform actions from a macro a certain amount of times"));
                sender.sendMessage(Color.format("&2> &3/FakePlayers action help &b- displays a help page for an action subcommand."));
                sender.sendMessage(Color.format("&2> &3/FakePlayers list &b- displays a fake player list"));
                sender.sendMessage(Color.format("&2> &3/FakePlayers reload &b- reloads config.yml"));
                sender.sendMessage("");
                sender.sendMessage(Color.format("&2[&aFake players - &bHelp (Page 1/1)&2]"));
                sender.sendMessage("");
            } else {
                switch (args[0]) {
                    case "summon":
                        if (args.length == 2) {
                            summon(sender, args[1], args);
                        } else if (args.length == 3) {
                            summon(sender, args[1], Integer.parseInt(args[2]), args);
                        } else {
                            Bukkit.dispatchCommand(sender, "npc");
                        }
                        break;
                    case "disband":
                        if (args.length == 2) {
                            disband(sender, args[1], args);
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
                        Bukkit.getPluginManager().disablePlugin(NPC.getPlugin());
                        Bukkit.getPluginManager().getPlugin("FakePlayers").reloadConfig();
                        Bukkit.getPluginManager().enablePlugin(NPC.getPlugin());
                        sender.sendMessage(NPC.getConfigMessage(NPC.getPlugin().config, "messages.reload", args));
                        break;
                    case "action":
                        if (args.length >= 3) {
                            if (!args[1].equalsIgnoreCase("All")) {
                                NPCPlayer player = NPCPlayer.getNPCPlayer(args[1]);

                                if (player != null) {
                                    action(sender, player, args);
                                } else {
                                    sender.sendMessage(NPC.getConfigMessage(NPC.getPlugin().config, "messages.action.invalid-player", args));
                                }
                            } else {
                                for (NPCPlayer player : NPCPlayer.getNPCPlayers()) {
                                    action(sender, player, args);
                                }
                            }
                        } else if (args[1].equals("help")) {
                            sender.sendMessage("");
                            sender.sendMessage(Color.format("&2[&aFake players - &bHelp (Action)&2]"));
                            sender.sendMessage("");
                            sender.sendMessage(Color.format("&2> &3/FakePlayers action (Name/All) perform &b- makes fake players perform all actions from their list"));
                            sender.sendMessage(Color.format("&2> &3/FakePlayers action (Name/All) perform (Number) &b- makes fake players perform all actions from their list a certain amount of times"));
                            sender.sendMessage(Color.format("&2> &3/FakePlayers action (Name/All) clear &b- removes all fake player's actions"));
                            sender.sendMessage("");
                            sender.sendMessage(Color.format("&2[&aFake players - &bHelp (Action)&2]"));
                            sender.sendMessage("");
                        } else {
                            Bukkit.dispatchCommand(sender, "fakeplayers");
                        }
                        break;
                    default:
                        Bukkit.dispatchCommand(sender, "npc");
                        break;
                }
            }
        }
        else {
            sender.sendMessage(NPC.getConfigMessage(NPC.getPlugin().config, "messages.no-permissions", args));
        }
        return false;
    }

    private void summon(CommandSender sender, String name, int number, String[] args) {
        if (number == 1) {
            if (NPCPlayer.summon(name)) {
                sender.sendMessage(NPC.getConfigMessage(NPC.getPlugin().config, "messages.summon.success-one", args));
            } else {
                sender.sendMessage(NPC.getConfigMessage(NPC.getPlugin().config, "messages.summon.failed", args));
            }
        } else if (number < 1) {
            sender.sendMessage(NPC.getConfigMessage(NPC.getPlugin().config, "messages.summon.incorrect-number", args));
        } else {
            int addition = 0;
            for (int i = 1; NPCPlayer.getNPCPlayer(name + i) != null; i++) {
                addition = i;
            }

            for (int i = addition; i < number + addition; i++) {
                final int I = i;
                Bukkit.getScheduler().scheduleSyncDelayedTask(NPC.getPlugin(), () -> NPCPlayer.summon(name + (I + 1)), I * NPC.getPlugin().config.getInt("tick-delay-between-joins"));
            }
            sender.sendMessage(NPC.getConfigMessage(NPC.getPlugin().config, "messages.summon.trying-amount", args));
        }
    }
    private void summon(CommandSender sender, String name, String[] args) {
        summon(sender, name, 1, args);
    }

    private void disband(CommandSender sender, String name, String[] args) {
        if (name.equalsIgnoreCase("All")) {
            List<NPCPlayer> copy = new ArrayList<>(NPCPlayer.getNPCPlayers());
            for (NPCPlayer player : copy) {
                player.removePlayer();
            }
            sender.sendMessage(NPC.getConfigMessage(NPC.getPlugin().config, "messages.disband.all-disbanded-success", args));
        } else {
            NPCPlayer npePlayer = NPCPlayer.getNPCPlayer(name);

            if (npePlayer != null) {
                npePlayer.removePlayer();
                sender.sendMessage(NPC.getConfigMessage(NPC.getPlugin().config, "messages.disband.one-disbanded-success", args));
            } else {
                sender.sendMessage(NPC.getConfigMessage(NPC.getPlugin().config, "messages.disband.failed", args));
            }
        }
    }

    private void list(CommandSender sender) {
        sender.sendMessage(Color.format("&aFake Players (" + NPCPlayer.getAmount() + "):"));

        StringBuilder list = new StringBuilder();

        for (NPCPlayer player : NPCPlayer.getNPCPlayers()) {
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

    private void action(CommandSender sender, NPCPlayer player, String[] args) {
        switch (args[2].toLowerCase()) {
            case "perform":
                new Thread(() -> {
                    if (args.length == 3) {
                        player.perform(1);
                    } else {
                        player.perform(Integer.parseInt(args[3]));
                    }
                }).start();
                break;
            case "wait":
                if (args.length == 4) {
                    player.addAction(new ActionWait(Long.parseLong(args[3])));
                    sender.sendMessage(NPC.getConfigMessage(NPC.getPlugin().config, "messages.action.action-success", args));
                } else {
                    sender.sendMessage("");
                    sender.sendMessage(Color.format("&2[&aFake players - &bHelp (Action: Wait)&2]"));
                    sender.sendMessage("");
                    sender.sendMessage(Color.format("&2> &3/FakePlayers action (Name/All) Wait (Milliseconds) &b- makes Fake Player wait a certain amount of milliseconds"));
                    sender.sendMessage("");
                    sender.sendMessage(Color.format("&2[&aFake players - &bHelp (Action: Wait)&2]"));
                    sender.sendMessage("");
                }
                break;
        }
    }
}
