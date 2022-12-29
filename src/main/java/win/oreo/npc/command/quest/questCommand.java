package win.oreo.npc.command.quest;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import win.oreo.npc.Main;
import win.oreo.npc.util.quest.Quest;
import win.oreo.npc.util.quest.questType;
import win.oreo.npc.util.quest.QuestUtil;

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
                String[] strings = new String[10];
                switch(args[0]) {
                    case "add":
                        if (args.length == 6) {
                            if (player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                                player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.wrong-item-hand", strings));
                                return false;
                            }
                            if (questType.valueOf(args[2]) == null) {
                                player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.wrong-quest", strings));
                                return false;
                            }
                            Quest quest = new Quest(UUID.randomUUID(), args[1], questType.valueOf(args[2]),
                                    args[3], Integer.parseInt(args[4]), player.getInventory().getItemInMainHand(),
                                    args[5]);
                            questUtil.addQuest(quest);
                            questRegisterMsg(player, quest);
                        }
                        else if (args.length == 7) {
                            if (Material.matchMaterial(args[5]) == null) {
                                player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.wrong-item", strings));
                                return false;
                            }
                            if (questType.valueOf(args[2]) == null) {
                                player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.wrong-quest", strings));
                                return false;
                            }
                            Quest quest = new Quest(UUID.randomUUID(), args[1], questType.valueOf(args[2]),
                                    args[3], Integer.parseInt(args[4]), new ItemStack(Material.valueOf(args[5]),
                                    1), args[6]);
                            questUtil.addQuest(quest);
                            questRegisterMsg(player, quest);
                        }
                        break;
                    case "edit":
                        if (args.length == 3) {
                            if (UUID.fromString(args[1]) == null) {
                                player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.wrong-id", strings));
                                return false;
                            }
                            if (player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                                player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.wrong-item-hand", strings));
                                return false;
                            }
                            Quest quest = questUtil.getQuestByID(UUID.fromString(args[1]));
                            questUtil.setQuestReward(quest, player.getInventory().getItemInMainHand());
                            questRegisterMsg(player, quest);
                        }
                        else if (args.length == 4) {
                            if (UUID.fromString(args[1]) == null) {
                                player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.wrong-id", strings));
                                return false;
                            }
                            UUID id = UUID.fromString(args[1]);
                            Quest quest = questUtil.getQuestByID(id);
                            strings[0] = quest.getQuestName();
                            switch (args[2]) {
                                case "name":
                                    questUtil.setQuestName(quest, args[3]);
                                    strings[1] = quest.getQuestName();
                                    player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.edit-name", strings));
                                    break;
                                case "type":
                                    if (questType.valueOf(args[3]) == null) {
                                        player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.wrong-quest", strings));
                                        return false;
                                    }
                                    questUtil.setQuestType(quest, questType.valueOf(args[3]));
                                    strings[1] = quest.getQuestType().toString();
                                    player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.edit-type", strings));
                                    break;
                                case "goal":
                                    questUtil.setQuestGoal(quest, Integer.parseInt(args[3]));
                                    strings[1] = String.valueOf(quest.getQuestGoal());
                                    player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.edit-goal", strings));
                                    break;
                                case "reward":
                                    if (Material.valueOf(args[4]) == null) {
                                        player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.wrong-item", strings));
                                        return false;
                                    }
                                    questUtil.setQuestReward(quest, new ItemStack(Material.matchMaterial(args[3]), 1));
                                    strings[1] = quest.getQuestReward().toString();
                                    player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.edit-rewd", strings));
                                    break;
                                case "target":
                                    switch (quest.getQuestType()) {
                                        case HUNT:
                                            questUtil.setQuestTarget(quest, Material.matchMaterial(args[3]));
                                            break;
                                        case COLLECT:
                                            questUtil.setQuestTarget(quest, EntityType.fromName(args[3]));
                                            break;
                                        default:
                                            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.wrong-target", strings));
                                            return false;
                                    }
                                    strings[1] = quest.getQuestTarget().toString();
                                    player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.edit-trgt", strings));
                                case "description":
                                    strings[1] = quest.getQuestDescription();
                                    player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.edit-desc", strings));
                                    questUtil.setQuestDescription(quest, args[3]);
                                    break;
                                default:
                                    player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.wrong-command", strings));
                                    break;
                            }
                        }
                        break;
                    case "list":
                        for (Quest quest : questList) {
                            questListMsg(player, quest);
                        }
                        break;
                    case "remove":
                        if (args[1].equalsIgnoreCase("all")) {
                            questUtil.removeAllQuest();
                            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.remove-all", strings));
                            return false;
                        }
                        if (UUID.fromString(args[1]) == null) {
                            player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.wrong-id", strings));
                            return false;
                        }
                        UUID id = UUID.fromString(args[1]);
                        Quest quest = questUtil.getQuestByID(id);
                        strings[0] = quest.getQuestName();
                        questRemoveMsg(player, quest);
                        questUtil.removeQuest(quest);
                        player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.remove", strings));
                        break;
                    case "addrandom":
                        Quest rQuest = new Quest(UUID.randomUUID(), getRandomWord(7),
                                getRandomQuestType(), getRandomQuestType() == questType.HUNT ? getRandomEntityType().toString() : getRandomMaterial().toString(),
                                new Random().nextInt(64), new ItemStack(getRandomMaterial(), new Random().nextInt(64)), getRandomWord(7));
                        questUtil.addQuest(rQuest);
                        questRegisterMsg(player, rQuest);
                        break;
                    default :
                        player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.wrong-command", strings));
                        break;
                }
            }
        }
        return false;
    }

    public void questRegisterMsg(Player player, Quest quest) {
        String[] strings = new String[10];
        strings[0] = quest.getQuestName();
        strings[1] = quest.getQuestID().toString();
        strings[2] = quest.getQuestType().toString();
        strings[3] = quest.getQuestTarget().toString();
        strings[4] = String.valueOf(quest.getQuestGoal());
        strings[5] = quest.getQuestReward().toString();
        strings[6] = quest.getQuestDescription();
        player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.quest-register", strings));
    }

    public void questRemoveMsg(Player player, Quest quest) {
        String[] strings = new String[10];
        strings[0] = quest.getQuestName();
        strings[1] = quest.getQuestID().toString();
        strings[2] = quest.getQuestType().toString();
        strings[3] = quest.getQuestTarget().toString();
        strings[4] = String.valueOf(quest.getQuestGoal());
        strings[5] = quest.getQuestReward().toString();
        strings[6] = quest.getQuestDescription();
        player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.quest-remove", strings));
    }

    public void questListMsg(Player player, Quest quest) {
        String[] strings = new String[10];
        strings[0] = quest.getQuestName();
        strings[1] = quest.getQuestID().toString();
        strings[2] = quest.getQuestType().toString();
        strings[3] = quest.getQuestTarget().toString();
        strings[4] = String.valueOf(quest.getQuestGoal());
        strings[5] = quest.getQuestReward().toString();
        strings[6] = quest.getQuestDescription();
        player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.quest-list", strings));
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
