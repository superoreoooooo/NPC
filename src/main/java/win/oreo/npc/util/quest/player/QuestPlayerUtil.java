package win.oreo.npc.util.quest.player;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import win.oreo.npc.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuestPlayerUtil {
    public static List<QuestPlayer> questPlayerList = new ArrayList<>();

    private Main plugin;

    public QuestPlayerUtil() {
        this.plugin = JavaPlugin.getPlugin(Main.class);
    }

    public Integer[] get0s() {
        Integer[] integers = new Integer[2];
        integers[0] = 0;
        integers[1] = 0;
        return integers;
    }

    public void saveAllQuestPlayer() {
        for (QuestPlayer questPlayer : questPlayerList) {
            for (String npcName : questPlayer.getQuestPlayerMap().keySet()) {
                Integer[] list = questPlayer.getQuestPlayerMap().get(npcName);
                plugin.questYml.getConfig().set("player." + questPlayer.getPlayer().getName() + ".npc." + npcName + ".done", list[0]);
                plugin.questYml.getConfig().set("player." + questPlayer.getPlayer().getName() + ".npc." + npcName + ".proceeding", list[1]);
            }
            plugin.questYml.saveConfig();
        }
    }

    public void createQuestPlayer(OfflinePlayer player) {
        HashMap<String, Integer[]> map = new HashMap<>();
        QuestPlayer questPlayer = new QuestPlayer(player, map);
        questPlayerList.add(questPlayer);
    }

    public void addNpc(OfflinePlayer player, String npcName) {
        if (getQuestPlayer(player).getQuestPlayerMap() == null) createQuestPlayer(player);
        if (getQuestPlayer(player).getQuestPlayerMap().containsKey(npcName)) return;
        getQuestPlayer(player).getQuestPlayerMap().put(npcName, get0s());
    }

    public void setQuestPlayerComplete(OfflinePlayer player, String npcName, int done) {
        if (!getQuestPlayer(player).getQuestPlayerMap().containsKey(npcName)) return;
        Integer[] integers = getQuestPlayer(player).getQuestPlayerMap().get(npcName);
        integers[0] = done;
        getQuestPlayer(player).getQuestPlayerMap().put(npcName, integers);
    }

    public void setQuestPlayerStatus(OfflinePlayer player, String npcName, int proceeding) {
        if (!getQuestPlayer(player).getQuestPlayerMap().containsKey(npcName)) return;
        Integer[] integers = getQuestPlayer(player).getQuestPlayerMap().get(npcName);
        integers[1] = proceeding;
        getQuestPlayer(player).getQuestPlayerMap().put(npcName, integers);
    }

    public int getQuestPlayerComplete(OfflinePlayer player, String npcName) {
        if (!getQuestPlayer(player).getQuestPlayerMap().containsKey(npcName)) return -1;
        Integer[] ints = getQuestPlayer(player).getQuestPlayerMap().get(npcName);
        return ints[0];
    }

    public int getQuestPlayerStatus(OfflinePlayer player, String npcName) {
        if (!getQuestPlayer(player).getQuestPlayerMap().containsKey(npcName)) return -1;
        Integer[] ints = getQuestPlayer(player).getQuestPlayerMap().get(npcName);
        return ints[1];
    }

    public void removeNpc(OfflinePlayer player, String npcName) {
        getQuestPlayer(player).getQuestPlayerMap().remove(npcName);
    }

    public void removeAllQuestPlayer() {
        plugin = JavaPlugin.getPlugin(Main.class);
        for (QuestPlayer questPlayer : questPlayerList) {
            plugin.questYml.getConfig().set("player." + questPlayer.getPlayer().getName(), null);
            plugin.questYml.saveConfig();
        }
        questPlayerList.clear();
    }

    public QuestPlayer getQuestPlayer(OfflinePlayer player) {
        for (QuestPlayer questPlayer : questPlayerList) {
            if (questPlayer.getPlayer().equals(player)) {
                return questPlayer;
            }
        }
        return null;
    }
}
