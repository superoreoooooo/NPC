package win.oreo.npc.util.quest.player;

import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Set;

public class QuestPlayer {
    private OfflinePlayer player;
    private HashMap<String, Integer> questProceedingMap; //npc / proceeding
    private HashMap<String, Integer> questProgressMap; //npc / progress
    private Set<String> questCompleteSet;

    public QuestPlayer(OfflinePlayer player, HashMap<String, Integer> questProceedingMap, Set<String> questCompleteList, HashMap<String, Integer> questProgressMap) {
        this.player = player;
        this.questProceedingMap = questProceedingMap;
        this.questCompleteSet = questCompleteList;
        this.questProgressMap = questProgressMap;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public HashMap<String, Integer> getQuestProceedingMap() {
        return questProceedingMap;
    }

    public Set<String> getQuestCompleteSet() {
        return questCompleteSet;
    }

    public HashMap<String, Integer> getQuestProgressMap() {
        return questProgressMap;
    }
}
