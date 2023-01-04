package win.oreo.npc.util.quest;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Quest<T> {
    private UUID questID;

    private String questName;
    private questType questType;
    private int questGoal;
    private ItemStack questReward;
    private T questTarget;

    public Quest(UUID questID, String questName, questType questType, T target, int questGoal, ItemStack questReward) {
        this.questID = questID;
        this.questName = questName;
        this.questType = questType;
        this.questTarget = target;
        this.questGoal = questGoal;
        this.questReward = questReward;
    }

    public UUID getQuestID() {
        return questID;
    }

    public String getQuestName() {
        return questName;
    }

    public questType getQuestType() {
        return questType;
    }

    public T getQuestTarget() {
        return questTarget;
    }

    public int getQuestGoal() {
        return questGoal;
    }

    public ItemStack getQuestReward() {
        return questReward;
    }

    public void setQuestName(String questName) {
        this.questName = questName;
    }

    public void setQuestType(questType questType) {
        this.questType = questType;
    }

    public void setQuestTarget(T questTarget) {
        this.questTarget = questTarget;
    }

    public void setQuestGoal(int questGoal) {
        this.questGoal = questGoal;
    }

    public void setQuestReward(ItemStack questReward) {
        this.questReward = questReward;
    }
}
