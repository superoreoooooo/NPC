package win.oreo.npc.util.quest;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import win.oreo.npc.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QuestUtil {
    public static List<Quest> questList = new ArrayList<>();

    private Main plugin;

    public QuestUtil() {
        this.plugin = Main.getPlugin();
    }

    public void saveAllQuest() {
        for (Quest quest : questList) {
            plugin.questYml.getConfig().set("quest." + quest.getQuestID().toString() + ".name", quest.getQuestName());
            plugin.questYml.getConfig().set("quest." + quest.getQuestID().toString() + ".type", quest.getQuestType().toString());
            plugin.questYml.getConfig().set("quest." + quest.getQuestID().toString() + ".target", quest.getQuestTarget());
            plugin.questYml.getConfig().set("quest." + quest.getQuestID().toString() + ".goal", quest.getQuestGoal());
            plugin.questYml.getConfig().set("quest." + quest.getQuestID().toString() + ".reward", quest.getQuestReward());
            plugin.questYml.saveConfig();
            Bukkit.getConsoleSender().sendMessage("Quest saved / UUID : " + quest.getQuestID() + " Name : " + quest.getQuestName() + " Type : " + quest.getQuestType());
        }
    }

    public void addQuest(Quest quest) {
        if (questList.contains(quest)) return;
        questList.add(quest);
    }

    public Quest getQuestByID(UUID uuid) {
        for (Quest quest : questList) {
            if (quest.getQuestID().equals(uuid)) return quest;
        }
        return null;
    }

    public void removeAllQuest() {
        plugin = JavaPlugin.getPlugin(Main.class);
        for (Quest quest : questList) {
            plugin.questYml.getConfig().set("quest." + quest.getQuestID().toString(), null);
            plugin.questYml.saveConfig();
        }
        questList.clear();
    }

    public void removeQuest(Quest quest) {
        questList.remove(quest);
    }

    public void setQuestName(Quest quest, String name) {
        quest.setQuestName(name);
    }

    public void setQuestType(Quest quest, questType type) {
        quest.setQuestType(type);
    }

    public void setQuestGoal(Quest quest, int goal) {
        quest.setQuestGoal(goal);
    }

    public void setQuestReward(Quest quest, ItemStack reward) {
        quest.setQuestReward(reward);
    }

    public void setQuestTarget(Quest quest, Object target) {
        quest.setQuestTarget(target);
    }
}
