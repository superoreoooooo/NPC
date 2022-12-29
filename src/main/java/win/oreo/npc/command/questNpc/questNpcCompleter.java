package win.oreo.npc.command.questNpc;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import win.oreo.npc.util.npc.NPCPlayer;
import win.oreo.npc.util.quest.Quest;
import win.oreo.npc.util.quest.QuestUtil;
import win.oreo.npc.util.quest.npc.QuestNpc;
import win.oreo.npc.util.quest.npc.QuestNpcUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class questNpcCompleter implements TabCompleter {
    private QuestUtil questUtil;
    private QuestNpcUtil questNpcUtil;

    public questNpcCompleter() {
        this.questUtil = new QuestUtil();
        this.questNpcUtil = new QuestNpcUtil();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (sender instanceof Player player) {
            switch (args.length) {
                case 1:
                    completions.add("list");
                    completions.add("remove");
                    completions.add("add");
                    break;
                case 2:
                    switch (args[0]) {
                        case "add":
                            for (QuestNpc npc : QuestNpcUtil.questNpcList) {
                                completions.add(npc.getNpcName());
                            }
                            for (NPCPlayer npc : NPCPlayer.getNPCPlayerList()) {
                                if (completions.contains(npc.getName())) continue;
                                completions.add(npc.getName());
                            }
                            break;
                        case "list":
                        case "remove":
                            for (QuestNpc npc : QuestNpcUtil.questNpcList) {
                                completions.add(npc.getNpcName());
                            }
                            break;
                    }
                    break;
                case 3:
                    switch (args[0]) {
                        case "add":
                            List<UUID> questIDs = new ArrayList<>();
                            QuestUtil.questList.forEach(quest -> questIDs.add(quest.getQuestID()));
                            completions.add("all");
                            for (UUID uuid : questIDs) {
                                completions.add(uuid.toString());
                            }
                            break;
                        case "remove":
                            QuestNpc npc = questNpcUtil.getQuestNpc(args[1]);
                            HashMap<Integer, Quest> questMap = npc.getQuestMap();
                            completions.add("all");
                            for (Quest quest : questMap.values()) {
                                completions.add(quest.getQuestID().toString());
                            }
                            break;
                    }
                    break;
            }
        }
        return completions;
    }
}
