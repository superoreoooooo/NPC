package xyz.oreodev.npc.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import xyz.oreodev.npc.NPC;
import xyz.oreodev.npc.NPCPlayer;

public class DeathListener implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        NPCPlayer npcPlayer = NPCPlayer.getNPCPlayer(e.getEntity().getUniqueId());
        if (npcPlayer != null) {
            if (NPC.getPlugin().usesPaper()) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(NPC.getPlugin(), () -> e.getEntity().spigot().respawn(), 20);
            }
        }
    }
}
