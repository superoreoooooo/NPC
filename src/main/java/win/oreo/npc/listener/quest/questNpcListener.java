package win.oreo.npc.listener.quest;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import win.oreo.npc.Main;
import win.oreo.npc.command.npc.NPCCommand;
import win.oreo.npc.util.quest.Quest;
import win.oreo.npc.util.quest.QuestInventory;
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

    private static List<Inventory> inventories = new ArrayList<>();
    private static List<Player> coolDown = new ArrayList<>();

    public questNpcListener() {
        this.questNpcUtil = new QuestNpcUtil();
        this.questPlayerUtil = new QuestPlayerUtil();
    }

    public void delay(Player player) {
        coolDown.add(player);
        Bukkit.getScheduler().runTaskLaterAsynchronously(JavaPlugin.getPlugin(Main.class), () -> coolDown.remove(player), 10);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (NPCCommand.editorList.contains((Player)e.getWhoClicked())) return;
        if (e.getClickedInventory() == null) {
            return;
        }
        if (e.getClickedInventory().getTitle().contains("의 보상")) {
            if (e.getCurrentItem() == null) return;
            if (e.getCurrentItem().getType().equals(Material.STAINED_GLASS_PANE)) {
                e.setCancelled(true);
                if (coolDown.contains((Player)e.getWhoClicked())) return;
                delay((Player)e.getWhoClicked());
            }
        }
        //e.getWhoClicked().sendMessage(e.getClickedInventory().getName() + " // " + e.getClick().toString() + " // " + e.getCurrentItem().getType().toString() + " // " + e.getCursor().getType().toString() + " // " + e.getAction().toString());
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (NPCCommand.editorList.contains((Player)e.getWhoClicked())) return;
        if (e.getCursor() == null) return;
        if (e.getCursor().getType().equals(Material.STAINED_GLASS_PANE)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onKill(EntityDeathEvent e) {
        LivingEntity victim = e.getEntity();
        Player player = victim.getKiller();

        //Bukkit.broadcastMessage(("killed by : " + victim.getKiller() + " killed : " + victim.getType().name()));

        QuestPlayer questPlayer = questPlayerUtil.getQuestPlayer(player);

        if (questPlayer == null) return;

        for (String str : questPlayer.getQuestProceedingMap().keySet()) {
            int proceeding = questPlayer.getQuestProceedingMap().get(str);
            if (proceeding == -1) continue;
            int progress = questPlayer.getQuestProgressMap().get(str);
            String[] strings = new String[4];
            Quest quest = questNpcUtil.getQuestNpc(str).getQuestMap().get(proceeding);
            if (quest == null) return;
            if (victim.getType().name().equals(quest.getQuestTarget().toString())) {
                questPlayer.getQuestProgressMap().put(str, progress + 1);
                strings[0] = quest.getQuestName();
                strings[1] = String.valueOf(progress + 1);
                strings[2] = String.valueOf(quest.getQuestGoal());
                strings[3] = quest.getQuestTarget().toString();
                player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.now", strings));
                if (progress >= quest.getQuestGoal() - 1) {
                    strings[0] = quest.getQuestName();
                    strings[1] = str;
                    player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.complete", strings));
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

                String[] strings = new String[6];

                if (questNpc == null) {
                    player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.add-quest", strings));
                    return;
                }

                if (questPlayerUtil.getQuestNpcs(player).contains(questNpc.getNpcName())) {
                    if (questPlayer.getQuestCompleteSet().contains(questNpc.getNpcName())) {
                        strings[0] = questNpc.getNpcName();
                        player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.complete-all", strings));
                        return;
                    }

                    boolean clear = false;

                    int proceeding = questPlayer.getQuestProceedingMap().get(questNpc.getNpcName());

                    if (proceeding == -1) {
                        proceeding = 0;
                        questPlayer.getQuestProceedingMap().put(questNpc.getNpcName(), proceeding);
                        strings[0] = questNpc.getNpcName();
                        player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.start", strings));
                        return;
                    }

                    Quest quest = questNpc.getQuestMap().get(proceeding);
                    String questName = quest.getQuestName();
                    Object target = questNpc.getQuestMap().get(proceeding).getQuestTarget();
                    int goal = questNpc.getQuestMap().get(proceeding).getQuestGoal();
                    questType type = questNpc.getQuestMap().get(proceeding).getQuestType();
                    int progress = questPlayer.getQuestProgressMap().get(str);
                    ItemStack reward = quest.getQuestReward();

                    strings[0] = questNpc.getNpcName();
                    strings[1] = questName;
                    strings[2] = target.toString();
                    strings[3] = String.valueOf(goal);
                    strings[4] = reward.getType().name();
                    strings[5] = String.valueOf(reward.getAmount());

                    //player.sendMessage("quest by " + str + " name : " + questName + " target : " + target + " goal : " + goal + " progress : (" + progress + "/" + goal + ")");

                    switch (type) {
                        case HUNT -> {
                            if (progress >= goal) {
                                clear = true;
                            }
                        }
                        case COLLECT -> {
                            if (player.getInventory().getItemInMainHand().getType().equals(Material.getMaterial(target.toString())) && player.getInventory().getItemInMainHand().getAmount() >= goal) {
                                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - goal);
                                clear = true;
                            }
                        }
                    }

                    if (!clear) {
                        switch (type) {
                            case HUNT -> player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.data-hunt", strings));
                            case COLLECT -> player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.data-collect", strings));
                        }
                        return;
                    }

                    strings[0] = quest.getQuestName();
                    strings[1] = reward.getType().name();
                    strings[2] = String.valueOf(reward.getAmount());

                    player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.reward", strings));
                    proceeding++;

                    QuestInventory questInventory = new QuestInventory(reward, questNpc.getNpcName());
                    Inventory inventory = questInventory.getInventory();
                    inventories.add(inventory);
                    player.openInventory(inventory);

                    strings[1] = questNpc.getNpcName();

                    if (questNpc.getQuestMap().size() == proceeding) {
                        questPlayerUtil.setQuestNpcComplete(player, questNpc.getNpcName(), true);
                        strings[0] = questNpc.getNpcName();
                        player.sendMessage(Main.getConfigMessage(Main.getPlugin().config, "messages.quest.complete-all", strings));
                    }
                    questPlayerUtil.setQuestProceeding(player, questNpc.getNpcName(), proceeding);
                    questPlayerUtil.setQuestProgress(player, questNpc.getNpcName(), 0);
                } else {
                    questPlayerUtil.addNpc(player, questNpc.getNpcName());
                }
            }
        }
    }
}
