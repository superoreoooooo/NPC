package win.oreo.npc.util.quest.player;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import win.oreo.npc.Main;

import java.util.*;

public class QuestPlayerUtil {
    public static List<QuestPlayer> questPlayerList = new ArrayList<>();

    private Main plugin;

    public QuestPlayerUtil() {
        this.plugin = JavaPlugin.getPlugin(Main.class);
    }

    public void saveAllQuestPlayer() {
        for (QuestPlayer questPlayer : questPlayerList) {
            for (String npcName : questPlayer.getQuestProceedingMap().keySet()) {
                plugin.questYml.getConfig().set("player." + questPlayer.getPlayer().getName() + ".npc." + npcName + ".proceeding", questPlayer.getQuestProceedingMap().get(npcName));
                plugin.questYml.getConfig().set("player." + questPlayer.getPlayer().getName() + ".npc." + npcName + ".progress", questPlayer.getQuestProgressMap().get(npcName));
                if (questPlayer.getQuestCompleteSet().contains(npcName)) {
                    plugin.questYml.getConfig().set("player." + questPlayer.getPlayer().getName() + ".npc." + npcName + ".complete", true);
                } else {
                    plugin.questYml.getConfig().set("player." + questPlayer.getPlayer().getName() + ".npc." + npcName + ".complete", false);
                }
            }
            plugin.questYml.saveConfig();
        }
    }

    public void createQuestPlayer(OfflinePlayer player) {
        HashMap<String, Integer> map = new HashMap<>();
        HashMap<String, Integer> map1 = new HashMap<>();
        Set<String> list = new HashSet<>();
        QuestPlayer questPlayer = new QuestPlayer(player, map, list, map1);
        questPlayerList.add(questPlayer);
    }

    public void addNpc(OfflinePlayer player, String npcName) {
        if (getQuestPlayer(player).getQuestProceedingMap() == null) createQuestPlayer(player);
        if (getQuestPlayer(player).getQuestProceedingMap().containsKey(npcName)) return;
        getQuestPlayer(player).getQuestProceedingMap().put(npcName, -1);
        getQuestPlayer(player).getQuestProgressMap().put(npcName, 0);
    }

    public void setQuestProgress(OfflinePlayer player, String npcName, int progress) {
        if (getQuestPlayer(player).getQuestProgressMap() == null) return;
        getQuestPlayer(player).getQuestProgressMap().put(npcName, progress);
    }

    public int getQuestProgress(OfflinePlayer player, String npcName) {
        if (getQuestPlayer(player).getQuestProgressMap() == null) return 0;
        return getQuestPlayer(player).getQuestProgressMap().get(npcName);
    }

    public void setQuestNpcComplete(OfflinePlayer player, String npcName, boolean bool) {
        if (getQuestPlayer(player).getQuestCompleteSet() == null) return;
        if (bool) getQuestPlayer(player).getQuestCompleteSet().add(npcName);
        else getQuestPlayer(player).getQuestCompleteSet().remove(npcName);
    }

    public Boolean getQuestNpcComplete(OfflinePlayer player, String npcName) {
        if (getQuestPlayer(player).getQuestCompleteSet().contains(npcName)) return true;
        else return false;
    }

    public void setQuestProceeding(OfflinePlayer player, String npcName, int proceeding) {
        if (!getQuestPlayer(player).getQuestProceedingMap().containsKey(npcName)) return;
        getQuestPlayer(player).getQuestProceedingMap().put(npcName, proceeding);
    }

    public int getQuestProceeding(OfflinePlayer player, String npcName) {
        if (!getQuestPlayer(player).getQuestProceedingMap().containsKey(npcName)) return -1;
        return getQuestPlayer(player).getQuestProceedingMap().get(npcName);
    }

    public Set<String> getQuestNpcs(OfflinePlayer player) {
        return getQuestPlayer(player).getQuestProceedingMap().keySet();
    }

    public void removeNpc(OfflinePlayer player, String npcName) {
        getQuestPlayer(player).getQuestProceedingMap().remove(npcName);
        setQuestNpcComplete(player, npcName, false);
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
