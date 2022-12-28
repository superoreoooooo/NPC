package win.oreo.npc.command.questPlayer;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import win.oreo.npc.util.quest.player.QuestPlayer;
import win.oreo.npc.util.quest.player.QuestPlayerUtil;

public class questPlayerCommand implements CommandExecutor {
    private QuestPlayerUtil questPlayerUtil;

    public questPlayerCommand() {
        this.questPlayerUtil = new QuestPlayerUtil();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            switch (args.length) {
                case 1:
                    switch (args[0]) {
                        case "list" -> {
                            for (QuestPlayer questPlayer : QuestPlayerUtil.questPlayerList) {
                                for (String npc : questPlayer.getQuestPlayerMap().keySet()) {
                                    Integer[] list = questPlayer.getQuestPlayerMap().get(npc);
                                    player.sendMessage("npc : " + npc + " complete : " + list[0] + " status : " + list[1]);
                                }
                            }
                        }
                    }
                    break;
                case 2:
                    switch (args[0]) {
                        case "create" -> questPlayerUtil.createQuestPlayer(Bukkit.getOfflinePlayer(args[1]));
                        case "remove" -> {
                            if (args[1].equals("all")) questPlayerUtil.removeAllQuestPlayer();
                        }
                    }
                    break;
                case 3:
                    switch (args[0]) {
                        case "add" -> {
                            if (questPlayerUtil.getQuestPlayer(Bukkit.getOfflinePlayer(args[1])) == null) {
                                questPlayerUtil.createQuestPlayer(Bukkit.getOfflinePlayer(args[1]));
                            }
                            questPlayerUtil.addNpc(Bukkit.getOfflinePlayer(args[1]), args[2]);
                        }
                        case "remove" -> {
                            switch (args[1]) {
                                default -> questPlayerUtil.removeNpc(Bukkit.getOfflinePlayer(args[1]), args[2]);
                            }
                        }
                    }
                    break;
                case 4:
                    switch (args[0]) {
                        case "get":
                            switch (args[3]) {
                                case "status" -> player.sendMessage("quest status : " + questPlayerUtil.getQuestPlayerStatus(Bukkit.getOfflinePlayer(args[1]), args[2]));
                                case "complete" -> player.sendMessage("quest complete : " + questPlayerUtil.getQuestPlayerComplete(Bukkit.getOfflinePlayer(args[1]), args[2]));
                            }
                            break;
                    }
                    break;
                case 5:
                    switch (args[0]) {
                        case "set":
                            switch (args[3]) {
                                case "status" -> questPlayerUtil.setQuestPlayerStatus(Bukkit.getOfflinePlayer(args[1]), args[2], Integer.parseInt(args[4]));
                                case "complete" -> questPlayerUtil.setQuestPlayerComplete(Bukkit.getOfflinePlayer(args[1]), args[2], Integer.parseInt(args[4]));
                            }
                            break;
                    }
            }
        }
        return false;
    }
}
