package win.oreo.npc.util.quest.player;

import org.bukkit.OfflinePlayer;

import java.util.HashMap;

public class QuestPlayer {
    private OfflinePlayer player;
    private HashMap<String, Integer[]> questPlayerMap;

    public QuestPlayer(OfflinePlayer player, HashMap<String, Integer[]> questPlayerMap) {
        this.player = player;
        this.questPlayerMap = questPlayerMap;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }
    public HashMap<String, Integer[]> getQuestPlayerMap() {
        return questPlayerMap;
    }
}
