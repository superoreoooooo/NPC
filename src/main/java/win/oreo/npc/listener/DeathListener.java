package win.oreo.npc.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import win.oreo.npc.Main;
import win.oreo.npc.util.npc.NPCPlayer;

public class DeathListener implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        NPCPlayer npcPlayer = NPCPlayer.getNPCPlayer(e.getEntity().getUniqueId());
        if (npcPlayer != null) {
            if (Main.getPlugin().usesPaper()) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> e.getEntity().spigot().respawn(), 20);
            }
        }
    }
}
