package win.oreo.npc.command.quest;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import win.oreo.npc.util.quest.Quest;
import win.oreo.npc.util.quest.questType;
import win.oreo.npc.util.quest.QuestUtil;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import static win.oreo.npc.util.quest.QuestUtil.questList;

public class questCommand implements CommandExecutor {
    private QuestUtil questUtil;

    public questCommand() {
        this.questUtil = new QuestUtil();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length > 0) {
                switch(args[0]) {
                    case "add":
                        if (args.length == 6) {
                            if (player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                                player.sendMessage("아이템이어쩌고");
                                return false;
                            }
                            if (questType.valueOf(args[2]) == null) {
                                player.sendMessage("퀘스트가어쩌고");
                                return false;
                            }
                            Quest quest = new Quest(UUID.randomUUID(), args[1], questType.valueOf(args[2]), args[3], Integer.parseInt(args[4]), player.getInventory().getItemInMainHand(), args[5]);
                            questUtil.addQuest(quest);
                            player.sendMessage("퀘스트 등록 / uuid : " + quest.getQuestID() + " name : " + quest.getQuestName() + " type : " + quest.getQuestType().toString() + " target : " + quest.getQuestTarget().toString() + " goal : " + quest.getQuestGoal() + " reward : " + quest.getQuestReward().getType().name() + " description : " + quest.getQuestDescription());
                        }
                        else if (args.length == 7) {
                            if (Material.matchMaterial(args[5]) == null) {
                                player.sendMessage("아이템이어쩌고");
                                return false;
                            }
                            if (questType.valueOf(args[2]) == null) {
                                player.sendMessage("퀘스트가어쩌고");
                                return false;
                            }
                            Quest quest = new Quest(UUID.randomUUID(), args[1], questType.valueOf(args[2]), args[3], Integer.parseInt(args[4]), new ItemStack(Material.valueOf(args[5]), 1), args[6]);
                            questUtil.addQuest(quest);
                            player.sendMessage("퀘스트 등록 / uuid : " + quest.getQuestID() + " name : " + quest.getQuestName() + " type : " + quest.getQuestType().toString() + " target : " + quest.getQuestTarget().toString() + " goal : " + quest.getQuestGoal() + " reward : " + quest.getQuestReward().getType().name() + " description : " + quest.getQuestDescription());
                        }
                        break;
                    case "edit":
                        if (args.length == 3) {
                            if (UUID.fromString(args[1]) == null) {
                                player.sendMessage("uuid가어쩌고");
                                return false;
                            }
                            if (player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                                player.sendMessage("아이템이어쩌고");
                                return false;
                            }
                            Quest quest = questUtil.getQuestByID(UUID.fromString(args[1]));
                            questUtil.setQuestReward(quest, player.getInventory().getItemInMainHand());
                            player.sendMessage("퀘스트 수정 / uuid : " + quest.getQuestID() + " name : " + quest.getQuestName() + " type : " + quest.getQuestType().toString() + " target : " + quest.getQuestTarget().toString() + " goal : " + quest.getQuestGoal() + " reward : " + quest.getQuestReward().getType().name() + " description : " + quest.getQuestDescription());
                        }
                        else if (args.length == 4) {
                            if (UUID.fromString(args[1]) == null) {
                                player.sendMessage("uuid가어쩌고");
                                return false;
                            }
                            UUID id = UUID.fromString(args[1]);
                            Quest quest = questUtil.getQuestByID(id);
                            switch (args[2]) {
                                case "name":
                                    questUtil.setQuestName(quest, args[3]);
                                    break;
                                case "type":
                                    if (questType.valueOf(args[3]) == null) {
                                        player.sendMessage("퀘스트가어쩌고");
                                        return false;
                                    }
                                    questUtil.setQuestType(quest, questType.valueOf(args[3]));
                                    break;
                                case "goal":
                                    questUtil.setQuestGoal(quest, Integer.parseInt(args[3]));
                                    break;
                                case "reward":
                                    if (Material.valueOf(args[4]) == null) {
                                        player.sendMessage("아이템이어쩌고");
                                        return false;
                                    }
                                    questUtil.setQuestReward(quest, new ItemStack(Material.matchMaterial(args[3]), 1));
                                    break;
                                case "target":
                                    switch (quest.getQuestType()) {
                                        case HUNT:
                                            questUtil.setQuestTarget(quest, Material.matchMaterial(args[3]));
                                            break;
                                        case COLLECT:
                                            questUtil.setQuestTarget(quest, EntityType.fromName(args[3]));
                                            break;
                                    }
                                case "description":
                                    questUtil.setQuestDescription(quest, args[3]);
                                    break;
                                default:
                                    player.sendMessage("제대로좀치세요제발");
                                    break;
                            }
                        }
                        break;
                    case "list":
                        for (Quest quest : questList) {
                            player.sendMessage("퀘스트 / uuid : " + quest.getQuestID() + " name : " + quest.getQuestName() + " type : " + quest.getQuestType().toString() + " target : " + quest.getQuestTarget().toString() + " goal : " + quest.getQuestGoal() + " reward : " + quest.getQuestReward().getType().name() + " description : " + quest.getQuestDescription());
                        }
                        break;
                    case "remove":
                        if (args[1].equalsIgnoreCase("all")) {
                            questUtil.removeAllQuest();
                            player.sendMessage("모든 퀘스트 삭제 완료");
                            return false;
                        }
                        if (UUID.fromString(args[1]) == null) {
                            player.sendMessage("uuid가어쩌고");
                            return false;
                        }
                        UUID id = UUID.fromString(args[1]);
                        Quest quest = questUtil.getQuestByID(id);
                        player.sendMessage("삭제된 퀘스트 / uuid : " + quest.getQuestID() + " name : " + quest.getQuestName() + " type : " + quest.getQuestType().toString() + " target : " + quest.getQuestTarget().toString() + " goal : " + quest.getQuestGoal() + " reward : " + quest.getQuestReward().getType().name() + " description : " + quest.getQuestDescription());
                        questUtil.removeQuest(quest);
                        player.sendMessage("삭제 완료");
                        break;
                    case "addrandom":
                        Quest rQuest = new Quest(UUID.randomUUID(), getRandomWord(7), getRandomQuestType(), getRandomQuestType() == questType.HUNT ? getRandomEntityType().toString() : getRandomMaterial().toString(), new Random().nextInt(64), new ItemStack(getRandomMaterial(), new Random().nextInt(64)), getRandomWord(7));
                        questUtil.addQuest(rQuest);
                        player.sendMessage("퀘스트 등록 / uuid : " + rQuest.getQuestID() + " name : " + rQuest.getQuestName() + " type : " + rQuest.getQuestType().toString() + " target : " + rQuest.getQuestTarget().toString() + " goal : " + rQuest.getQuestGoal() + " reward : " + rQuest.getQuestReward().getType().name() + " description : " + rQuest.getQuestDescription());
                        break;
                    default :
                        player.sendMessage("제대로좀치세요제발");
                        break;
                }
            }
        }
        return false;
    }

    public static Material getRandomMaterial() {
        return Material.values()[new Random().nextInt(Material.values().length)];
    }


    public static EntityType getRandomEntityType() {
        return EntityType.values()[new Random().nextInt(EntityType.values().length)];
    }

    public static questType getRandomQuestType() {
        return questType.values()[new Random().nextInt(questType.values().length)];
    }

    public static String getRandomWord(int length) {
        Random random = new Random();
        StringBuilder newWord = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int choice = random.nextInt(3);
            switch (choice) {
                case 0 -> newWord.append((char) (random.nextInt(25) + 97));
                case 1 -> newWord.append((char) (random.nextInt(25) + 65));
                case 2 -> newWord.append((char) (random.nextInt(10) + 48));
            }
        }
        return newWord.toString();
    }

}
