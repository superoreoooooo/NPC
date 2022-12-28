package win.oreo.npc.command.questNpc;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import win.oreo.npc.util.npc.NPCPlayer;
import win.oreo.npc.util.quest.Quest;
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                if (args[0].equals("list")) {
                    List<String> list = new ArrayList<>();
                    QuestNpcUtil.questNpcList.forEach(questNpc -> list.add(questNpc.getNpcName()));
                    player.sendMessage(list.toString());
                }
            }
            if (args.length == 2) {
                if (args[0].equals("remove") && args[1].equals("all")) {
                    questNpcUtil.removeAllQuestNpc();
                }
            }
            if (args.length == 3) {
                switch (args[0]) {
                    case "add":
                        if (NPCPlayer.getNPCPlayer(args[1]) == null) return false;
                        if (args[2].equals("all")) {
                            if (questNpcUtil.getQuestNpc(args[1]) == null) {
                                return false;
                            }
                            for (Quest quest : QuestUtil.questList) {
                                questNpcUtil.addQuest(args[1], quest);
                            }
                            return false;
                        }
                        if (questNpcUtil.getQuestNpc(args[1]) == null) {
                            questNpcUtil.createQuest(args[1], questUtil.getQuestByID(UUID.fromString(args[2])));
                            return false;
                        }
                        questNpcUtil.addQuest(args[1], questUtil.getQuestByID(UUID.fromString(args[2])));
                        break;
                    case "remove":
                        if (NPCPlayer.getNPCPlayer(args[1]) == null) return false;
                        if (questNpcUtil.getQuestNpc(args[1]) == null) {
                            return false;
                        }
                        if (args[2].matches("[0-9]+")) {
                            questNpcUtil.removeQuest(args[1], Integer.parseInt(args[2]));
                            return true;
                        }
                        if (args[2].equals("all")) {
                            questNpcUtil.clearQuest(args[1]);
                            return true;
                        }
                        questNpcUtil.removeQuest(args[1], questUtil.getQuestByID(UUID.fromString(args[2])));
                        break;
                }
            }
        }
        return false;
    }
}
