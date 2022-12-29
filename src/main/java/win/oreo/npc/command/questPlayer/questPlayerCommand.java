package win.oreo.npc.command.questPlayer;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import win.oreo.npc.Main;
import win.oreo.npc.util.quest.npc.QuestNpc;
import win.oreo.npc.util.quest.npc.QuestNpcUtil;
import win.oreo.npc.util.quest.player.QuestPlayer;
import win.oreo.npc.util.quest.player.QuestPlayerUtil;

public class questPlayerCommand implements CommandExecutor {
    private QuestPlayerUtil questPlayerUtil;
    private QuestNpcUtil questNpcUtil;

    public questPlayerCommand() {
        this.questPlayerUtil = new QuestPlayerUtil();
        this.questNpcUtil = new QuestNpcUtil();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            String[] strings = new String[10];
            switch (args.length) {
                case 1:
                    switch (args[0]) {
                        case "list" -> {
                            for (QuestPlayer questPlayer : QuestPlayerUtil.questPlayerList) {
                                for (String npc : questPlayer.getQuestProceedingMap().keySet()) {
                                    int proceeding = questPlayer.getQuestProceedingMap().get(npc);
                                    QuestNpc questNpc = questNpcUtil.getQuestNpc(npc);
                                    strings[0] = questNpc.getQuestMap().get(proceeding).getQuestName();
                                    strings[1] = String.valueOf(questPlayer.getQuestProgressMap().get(npc));
                                    strings[2] = questNpc.getQuestMap().get(proceeding).getQuestID().toString();
                                    player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.player.list", strings));
                                }
                            }
                        }
                    }
                    break;
                case 2:
                    switch (args[0]) {
                        case "create" -> {
                            questPlayerUtil.createQuestPlayer(Bukkit.getOfflinePlayer(args[1]));
                            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.player.test", strings));
                            strings[0] = args[1];
                            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.player.create", strings));
                        }
                        case "remove" -> {
                            if (args[1].equals("all")) questPlayerUtil.removeAllQuestPlayer();
                            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.player.remove-all", strings));
                        }
                    }
                    break;
                case 3:
                    switch (args[0]) {
                        case "add" -> {
                            strings[0] = args[1];
                            if (questPlayerUtil.getQuestPlayer(Bukkit.getOfflinePlayer(args[1])) == null) {
                                player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.player.no-exist", strings));
                                player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.player.create", strings));
                                questPlayerUtil.createQuestPlayer(Bukkit.getOfflinePlayer(args[1]));
                            }
                            questPlayerUtil.addNpc(Bukkit.getOfflinePlayer(args[1]), args[2]);
                            strings[1] = args[2];
                            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.player.add", strings));
                        }
                        case "remove" -> {
                            questPlayerUtil.removeNpc(Bukkit.getOfflinePlayer(args[1]), args[2]);
                            strings[0] = args[1];
                            strings[1] = args[2];
                            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.player.remove", strings));
                        }
                    }
                    break;
                case 4:
                    switch (args[0]) {
                        case "get":
                            strings[0] = args[1];
                            strings[1] = args[2];
                            switch (args[3]) {
                                case "proceeding" -> {
                                    strings[2] = String.valueOf(questPlayerUtil.getQuestProceeding(Bukkit.getOfflinePlayer(args[1]), args[2]));
                                    player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.player.get-proceeding", strings));
                                }
                                case "progress" -> {
                                    strings[2] = String.valueOf(questPlayerUtil.getQuestProgress(Bukkit.getOfflinePlayer(args[1]), args[2]));
                                    player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.player.get-progress", strings));
                                }
                                case "complete" -> {
                                    if (questPlayerUtil.getQuestNpcComplete(Bukkit.getOfflinePlayer(args[1]), args[2]))
                                        player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.player.get-complete-true", strings));
                                    else
                                        player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.player.get-complete-false", strings));

                                }
                            }
                            break;
                    }
                    break;
                case 5:
                    switch (args[0]) {
                        case "set":
                            strings[0] = args[1];
                            strings[1] = args[2];
                            strings[2] = args[4];
                            switch (args[3]) {
                                case "proceeding" -> {
                                    questPlayerUtil.setQuestProceeding(Bukkit.getOfflinePlayer(args[1]), args[2], Integer.parseInt(args[4]));
                                    player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.player.set-proceeding", strings));
                                }
                                case "progress" -> {
                                    questPlayerUtil.setQuestProgress(Bukkit.getOfflinePlayer(args[1]), args[2], Integer.parseInt(args[4]));
                                    player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.player.set-progress", strings));
                                }
                                case "complete" -> {
                                    questPlayerUtil.setQuestNpcComplete(Bukkit.getOfflinePlayer(args[1]), args[2], Boolean.parseBoolean(args[4]));
                                    player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.player.set-complete", strings));
                                }
                            }
                            break;
                    }
            }
        }
        return false;
    }
}
