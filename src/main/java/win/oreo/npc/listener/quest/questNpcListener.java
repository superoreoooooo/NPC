package win.oreo.npc.listener.quest;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import win.oreo.npc.Main;
import win.oreo.npc.util.quest.Quest;
import win.oreo.npc.util.quest.npc.QuestNpc;
import win.oreo.npc.util.quest.npc.QuestNpcUtil;
import win.oreo.npc.util.quest.player.QuestPlayer;
import win.oreo.npc.util.quest.player.QuestPlayerUtil;
import win.oreo.npc.util.quest.questType;

import java.util.ArrayList;
import java.util.List;

public class questNpcListener implements Listener {
    private QuestNpcUtil questNpcUtil;
    private QuestPlayerUtil questPlayerUtil;

    List<Player> coolDown = new ArrayList<>();

    public questNpcListener() {
        this.questNpcUtil = new QuestNpcUtil();
        this.questPlayerUtil = new QuestPlayerUtil();
    }

    public void delay(Player player) {
        coolDown.add(player);
        Bukkit.getScheduler().runTaskLaterAsynchronously(JavaPlugin.getPlugin(Main.class), () -> coolDown.remove(player), 10);
    }

    @EventHandler
    public void onKill(EntityDeathEvent e) {
        LivingEntity victim = e.getEntity();
        Player player = victim.getKiller();

        //Bukkit.broadcastMessage(("killed by : " + victim.getKiller() + " killed : " + victim.getType().name()));

        QuestPlayer questPlayer = questPlayerUtil.getQuestPlayer(player);

        if (questPlayer == null) return;

        for (String str : questPlayer.getQuestPlayerMap().keySet()) {
            int proceeding = questPlayer.getQuestPlayerMap().get(str);
            if (proceeding == -1) continue;
            int progress = questPlayer.getQuestProgressMap().get(str);
            Quest quest = questNpcUtil.getQuestNpc(str).getQuestMap().get(proceeding);
            if (victim.getType().name().equals(quest.getQuestTarget().toString())) {
                questPlayer.getQuestProgressMap().put(str, progress + 1);
                player.sendMessage("quest by " + str + " name : " + quest.getQuestName() + " target : " + quest.getQuestTarget() + " goal : " + quest.getQuestGoal() + " progress : (" + (progress + 1) + "/" + quest.getQuestGoal() + ")");
                if (progress >= quest.getQuestGoal()) {
                    player.sendMessage("quest done!");
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent e) {
        Player player = e.getPlayer();
        if (coolDown.contains(player)) return;
        delay(player);
        if (!e.getPlayer().isSneaking()) return;
        if (e.getRightClicked().getType().equals(EntityType.PLAYER)) {
            if (e.getRightClicked().hasMetadata("npc")) {
                String str = e.getRightClicked().getMetadata("npc").get(0).asString();
                /**
                player.sendMessage("[questNPC] " + str);
                List<Quest> questList = questNpcUtil.getQuestNpc(str).getQuestMap().values().stream().toList();
                for (Quest quest : questList) {
                    player.sendMessage("name : " + quest.getQuestName() + " id : " + quest.getQuestID());
                } */

                if (questPlayerUtil.getQuestPlayer(player) == null) {
                    questPlayerUtil.createQuestPlayer(player);
                }

                QuestPlayer questPlayer = questPlayerUtil.getQuestPlayer(player);
                QuestNpc questNpc = questNpcUtil.getQuestNpc(str);

                if (questNpc == null) {
                    player.sendMessage("please add quest to npc");
                    return;
                }

                if (questPlayerUtil.getQuestNpcs(player).contains(questNpc.getNpcName())) {
                    if (questPlayer.getQuestCompleteSet().contains(questNpc.getNpcName())) {
                        player.sendMessage("you already done this npc's quests");
                        return;
                    }

                    boolean clear = false;

                    int proceeding = questPlayer.getQuestPlayerMap().get(questNpc.getNpcName());

                    if (proceeding == -1) {
                        proceeding = 0;
                        questPlayer.getQuestPlayerMap().put(questNpc.getNpcName(), proceeding);
                        player.sendMessage("hello?");
                        return;
                    }

                    Quest quest = questNpc.getQuestMap().get(proceeding);
                    String questName = quest.getQuestName();
                    Object target = questNpc.getQuestMap().get(proceeding).getQuestTarget();
                    int goal = questNpc.getQuestMap().get(proceeding).getQuestGoal();
                    questType type = questNpc.getQuestMap().get(proceeding).getQuestType();
                    int progress = questPlayer.getQuestProgressMap().get(str);

                    player.sendMessage("quest by " + str + " name : " + questName + " target : " + target + " goal : " + goal + " progress : (" + progress + "/" + goal + ")");

                    switch (type) {
                        case HUNT -> {
                            if (progress >= goal) {
                                clear = true;
                            }
                        }
                        case COLLECT -> {
                            ItemStack item = new ItemStack(Material.getMaterial(target.toString()), goal);
                            if (player.getInventory().getItemInMainHand().equals(item)) {
                                player.getInventory().remove(item);
                                clear = true;
                            }
                        }
                    }

                    if (!clear) return;
                    player.sendMessage("quest clear!");
                    proceeding++;
                    ItemStack reward = quest.getQuestReward();
                    player.getInventory().addItem(reward);

                    if (questNpc.getQuestMap().size() == proceeding) {
                        questPlayerUtil.setQuestNpcComplete(player, questNpc.getNpcName(), true);
                        player.sendMessage("all done!");
                    }
                    questPlayerUtil.setQuestPlayerProceeding(player, questNpc.getNpcName(), proceeding);
                    questPlayerUtil.setQuestProgress(player, questNpc.getNpcName(), 0);
                } else {
                    questPlayerUtil.addNpc(player, questNpc.getNpcName());
                }
            }
        }
    }
}
