package win.oreo.npc.util.quest.npc;

import org.bukkit.plugin.java.JavaPlugin;
import win.oreo.npc.Main;
import win.oreo.npc.util.quest.Quest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuestNpcUtil {
    public static List<QuestNpc> questNpcList = new ArrayList<>();

    private Main plugin;

    public QuestNpcUtil() {
        this.plugin = JavaPlugin.getPlugin(Main.class);
    }

    public void saveAllQuestNpc() {
        for (QuestNpc npc : questNpcList) {
            for (int i = 0; i < npc.getQuestMap().size(); i++) {
                plugin.questYml.getConfig().set("npc." + npc.getNpcName() + ".list." + i, npc.getQuestMap().get(i).getQuestID().toString());
            }
            plugin.questYml.getConfig().set("npc." + npc.getNpcName() + ".count", npc.getQuestMap().size());
            plugin.questYml.saveConfig();
        }
    }

    public void createQuest(String npcName, Quest quest) {
        HashMap<Integer, Quest> map = new HashMap<>();
        map.put(0, quest);
        QuestNpc npc = new QuestNpc(npcName, map);
        questNpcList.add(npc);
    }

    public void addQuest(String npcName, Quest quest) {
        getQuestNpc(npcName).getQuestMap().put(getQuestNpc(npcName).getQuestMap().size(), quest);
    }

    public void clearQuest(String npcName) {
        getQuestNpc(npcName).getQuestMap().clear();
    }

    public void removeQuest(String npcName, Quest quest) {
        if (!getQuestNpc(npcName).getQuestMap().containsValue(quest)) return;
        for (int i : getQuestNpc(npcName).getQuestMap().keySet()) {
            if (getQuestNpc(npcName).getQuestMap().get(i).equals(quest)) {
                int size = getQuestNpc(npcName).getQuestMap().size();
                getQuestNpc(npcName).getQuestMap().remove(i);
                for (int j = i + 1; j < size; j++) {
                    getQuestNpc(npcName).getQuestMap().put(j-1, getQuestNpc(npcName).getQuestMap().get(j));
                }
                getQuestNpc(npcName).getQuestMap().remove(size-1);
                break;
            }
        }
    }

    public void removeQuest(String npcName, int index) {
        if (!getQuestNpc(npcName).getQuestMap().containsKey(index)) return;
        int size = getQuestNpc(npcName).getQuestMap().size();
        getQuestNpc(npcName).getQuestMap().remove(index);
        for (int j = index + 1; j < size; j++) {
            getQuestNpc(npcName).getQuestMap().put(j-1, getQuestNpc(npcName).getQuestMap().get(j));
        }
        getQuestNpc(npcName).getQuestMap().remove(size-1);
    }

    public void removeAllQuestNpc() {
        plugin = JavaPlugin.getPlugin(Main.class);
        for (QuestNpc questNpc : questNpcList) {
            plugin.questYml.getConfig().set("npc." + questNpc.getNpcName(), null);
            plugin.questYml.saveConfig();
        }
        questNpcList.clear();
    }

    public QuestNpc getQuestNpc(String npcName) {
        for (QuestNpc questNpc : questNpcList) {
            if (questNpc.getNpcName().equals(npcName)) {
                return questNpc;
            }
        }
        return null;
    }
}
