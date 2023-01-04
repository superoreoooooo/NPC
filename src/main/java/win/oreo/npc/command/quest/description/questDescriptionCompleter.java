package win.oreo.npc.command.quest.description;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import win.oreo.npc.util.quest.Quest;
import win.oreo.npc.util.quest.QuestUtil;

import java.util.ArrayList;
import java.util.List;

import static win.oreo.npc.util.quest.QuestUtil.questList;

public class questDescriptionCompleter implements TabCompleter {
    private QuestUtil questUtil;

    public questDescriptionCompleter() {
        this.questUtil = new QuestUtil();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        switch (args.length) {
            case 1:
                completions.add("set");
                completions.add("get");
                completions.add("remove");
                break;
            case 2:
                for (Quest quest : questList) {
                    completions.add(quest.getQuestID().toString());
                }
        }
        return completions;
    }
}
