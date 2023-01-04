package win.oreo.npc.util.quest.description;


import org.bukkit.plugin.java.JavaPlugin;
import win.oreo.npc.Main;
import win.oreo.npc.util.quest.Quest;

import java.util.HashMap;
import java.util.List;

public class QuestDescriptionUtil {
    private Main plugin;

    public QuestDescriptionUtil() {
        this.plugin = JavaPlugin.getPlugin(Main.class);
    }

    public static HashMap<Quest, List<String>> descriptionMap = new HashMap<>();

    public void setDescription(Quest quest, List<String> strings) {
        descriptionMap.put(quest, strings);
    }

    public List<String> getDescription(Quest quest) {
        return descriptionMap.get(quest);
    }

    public void removeDescription(Quest quest) {
        descriptionMap.remove(quest);
    }

    public void saveDescription() {
        for (Quest quest : descriptionMap.keySet()) {
            plugin.questYml.getConfig().set("description." + quest.getQuestID().toString(), descriptionMap.get(quest));
        }
        plugin.questYml.saveConfig();
    }
}
