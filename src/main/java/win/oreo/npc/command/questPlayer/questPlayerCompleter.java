package win.oreo.npc.command.questPlayer;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import win.oreo.npc.util.quest.npc.QuestNpc;
import win.oreo.npc.util.quest.npc.QuestNpcUtil;
import win.oreo.npc.util.quest.player.QuestPlayerUtil;

import java.util.ArrayList;
import java.util.List;

public class questPlayerCompleter implements TabCompleter {
    private QuestPlayerUtil questPlayerUtil;

    public questPlayerCompleter() {
        this.questPlayerUtil = new QuestPlayerUtil();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (sender instanceof Player player) {
            switch (args.length) {
                case 1:
                    completions.add("create");
                    completions.add("add");
                    completions.add("set");
                    completions.add("remove");
                    completions.add("get");
                    break;
                case 2:
                    switch (args[0]) {
                        case "add":
                        case "set":
                        case "get":
                            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                                completions.add(offlinePlayer.getName());
                            }
                            break;
                        case "remove":
                            completions.add("all");
                            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                                completions.add(offlinePlayer.getName());
                            }
                            break;
                    }
                    break;
                case 3:
                    switch (args[0]) {
                        case "add":
                            for (QuestNpc npc : QuestNpcUtil.questNpcList) {
                                completions.add(npc.getNpcName());
                            }
                            break;
                        case "remove":
                        case "set":
                        case "get":
                            completions.addAll(questPlayerUtil.getQuestPlayer(player).getQuestPlayerMap().keySet());
                            break;
                    }
                    break;
                case 4:
                    switch (args[0]) {
                        case "set":
                        case "get":
                            completions.add("proceeding");
                            completions.add("complete");
                            break;
                    }
                    break;
            }
        }
        return completions;
    }
}
