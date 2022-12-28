package win.oreo.npc.util.quest.player;

import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Set;

public class QuestPlayer {
    private OfflinePlayer player;
    private HashMap<String, Integer> questPlayerMap; //npc / proceeding
    private HashMap<String, Integer> questProgressMap; //npc / progress
    private Set<String> questCompleteSet;

    public QuestPlayer(OfflinePlayer player, HashMap<String, Integer> questPlayerMap, Set<String> questCompleteList, HashMap<String, Integer> questProgressMap) {
        this.player = player;
        this.questPlayerMap = questPlayerMap;
        this.questCompleteSet = questCompleteList;
        this.questProgressMap = questProgressMap;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public HashMap<String, Integer> getQuestPlayerMap() {
        return questPlayerMap;
    }

    public Set<String> getQuestCompleteSet() {
        return questCompleteSet;
    }

    public HashMap<String, Integer> getQuestProgressMap() {
        return questProgressMap;
    }
}
