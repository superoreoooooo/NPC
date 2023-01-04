package win.oreo.npc.command.quest.description;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import win.oreo.npc.util.quest.Quest;
import win.oreo.npc.util.quest.QuestUtil;
import win.oreo.npc.util.quest.description.QuestDescriptionUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class questDescriptionCommand implements CommandExecutor {
    private QuestUtil questUtil;
    private QuestDescriptionUtil questDescriptionUtil;

    public questDescriptionCommand() {
        this.questUtil = new QuestUtil();
        this.questDescriptionUtil = new QuestDescriptionUtil();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player && args.length >= 2) {
            Quest quest = questUtil.getQuestByID(UUID.fromString(args[1]));
            if (quest == null) return false;
            switch (args[0]) {
                case "set":
                    if (args.length > 2) {
                        List<String> str = new ArrayList(Arrays.asList(Arrays.copyOfRange(args, 2, args.length)));
                        questDescriptionUtil.setDescription(quest, str);
                        player.sendMessage("added!");
                    }
                    break;
                case "get":
                    List<String> strings = questDescriptionUtil.getDescription(quest);
                    if (strings == null) return false;
                    for (int i = 0; i < strings.size();) {
                        player.sendMessage(strings.get(i++));
                    }
                    break;
                case "remove":
                    questDescriptionUtil.removeDescription(quest);
                    player.sendMessage("removed!");
                    break;
            }
        }
        return false;
    }
}
