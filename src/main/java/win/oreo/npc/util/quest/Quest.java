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
    private String questDescription;

    public Quest(UUID questID, String questName, questType questType, T target, int questGoal, ItemStack questReward, String description) {
        this.questID = questID;
        this.questName = questName;
        this.questType = questType;
        this.questTarget = target;
        this.questGoal = questGoal;
        this.questReward = questReward;
        this.questDescription = description;
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

    public String getQuestDescription() {
        return questDescription;
    }

    public void setQuestDescription(String questDescription) {
        this.questDescription = questDescription;
    }
}
