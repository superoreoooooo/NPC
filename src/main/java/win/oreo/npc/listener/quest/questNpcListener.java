package win.oreo.npc.listener.quest;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import win.oreo.npc.Main;
import win.oreo.npc.util.quest.Quest;
import win.oreo.npc.util.quest.npc.QuestNpcUtil;

import java.util.ArrayList;
import java.util.List;

public class questNpcListener implements Listener {
    private QuestNpcUtil questNpcUtil;

    List<Player> coolDown = new ArrayList<>();

    public questNpcListener() {
        this.questNpcUtil = new QuestNpcUtil();
    }

    public void delay(Player player) {
        coolDown.add(player);
        Bukkit.getScheduler().runTaskLaterAsynchronously(JavaPlugin.getPlugin(Main.class), () -> coolDown.remove(player), 10);
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
                player.sendMessage("[questNPC] " + str);
                List<Quest> questList = questNpcUtil.getQuestNpc(str).getQuestMap().values().stream().toList();
                for (Quest quest : questList) {
                    player.sendMessage("name : " + quest.getQuestName() + " id : " + quest.getQuestID());
                }
            }
        }
    }
}
