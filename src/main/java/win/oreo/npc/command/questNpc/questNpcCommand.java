package win.oreo.npc.command.questNpc;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import win.oreo.npc.Main;
import win.oreo.npc.util.npc.NPCPlayer;
import win.oreo.npc.util.quest.Quest;
import win.oreo.npc.util.quest.npc.QuestNpc;
import win.oreo.npc.util.quest.npc.QuestNpcUtil;
import win.oreo.npc.util.quest.QuestUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class questNpcCommand implements CommandExecutor {
    private QuestNpcUtil questNpcUtil;
    private QuestUtil questUtil;

    public questNpcCommand() {
        this.questNpcUtil = new QuestNpcUtil();
        this.questUtil = new QuestUtil();
    }

    public String getQuestNpcList() {
        List<String> list = new ArrayList<>();
        QuestNpcUtil.questNpcList.forEach(questNpc -> list.add(questNpc.getNpcName()));
        return list.toString();
    }

    public String getNpcQuests(QuestNpc questNpc) {
        List<String> quests = new ArrayList<>();
        questNpc.getQuestMap().values().forEach(quest -> quests.add(quest.getQuestID().toString()));
        return quests.toString();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String[] strings = new String[10];
        if (sender instanceof Player player) {
            if (args.length == 1) {
                if (args[0].equals("list")) {
                    strings[0] = getQuestNpcList();
                    player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.npc.list", strings));
                }
                return false;
            }
            if (args.length == 2) {
                if (args[0].equals("remove") && args[1].equals("all")) {
                    questNpcUtil.removeAllQuestNpc();
                    player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.npc.remove-all", strings));
                }
                if (args[0].equals("list")) {
                    if (NPCPlayer.getNPCPlayer(args[1]) == null) return false;
                    if (questNpcUtil.getQuestNpc(args[1]) == null) return false;
                    strings[0] = args[1];
                    strings[1] = getNpcQuests(questNpcUtil.getQuestNpc(args[1]));
                    player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.npc.list-npc", strings));
                }
                return false;
            }
            if (args.length == 3) {
                switch (args[0]) {
                    case "add":
                        if (NPCPlayer.getNPCPlayer(args[1]) == null) return false;
                        if (args[2].equals("all")) {
                            if (questNpcUtil.getQuestNpc(args[1]) == null) {
                                player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.npc.no-exist", strings));
                                return false;
                            }
                            for (Quest quest : QuestUtil.questList) {
                                questNpcUtil.addQuest(args[1], quest);
                            }
                            strings[0] = args[1];
                            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.npc.add-all", strings));
                            return false;
                        }
                        if (questNpcUtil.getQuestNpc(args[1]) == null) {
                            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.npc.no-exist", strings));
                            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.npc.creation", strings));
                            questNpcUtil.createQuest(args[1], questUtil.getQuestByID(UUID.fromString(args[2])));
                            return false;
                        }
                        questNpcUtil.addQuest(args[1], questUtil.getQuestByID(UUID.fromString(args[2])));
                        strings[0] = args[1];
                        strings[1] = args[2];
                        strings[2] = questUtil.getQuestByID(UUID.fromString(args[2])).getQuestName();
                        player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.npc.add", strings));
                        break;
                    case "remove":
                        if (NPCPlayer.getNPCPlayer(args[1]) == null) return false;
                        if (questNpcUtil.getQuestNpc(args[1]) == null) {
                            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.npc.no-exist", strings));
                            return false;
                        }
                        strings[0] = args[1];
                        strings[1] = args[2];
                        if (args[2].matches("[0-9]+")) {
                            questNpcUtil.removeQuest(args[1], Integer.parseInt(args[2]));
                            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.npc.remove-int", strings));
                            return true;
                        }
                        if (args[2].equals("all")) {
                            questNpcUtil.clearQuest(args[1]);
                            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.npc.remove-all", strings));
                            return true;
                        }
                        questNpcUtil.removeQuest(args[1], questUtil.getQuestByID(UUID.fromString(args[2])));
                        strings[2] = questUtil.getQuestByID(UUID.fromString(args[2])).getQuestName();
                        player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.npc.remove-qst", strings));
                        break;
                }
                return false;
            }
        }
        return false;
    }
}
