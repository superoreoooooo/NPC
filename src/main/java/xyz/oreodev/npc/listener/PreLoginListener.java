package xyz.oreodev.npc.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPreLoginEvent;
import xyz.oreodev.npc.util.npc.NPCPlayer;

public class PreLoginListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void preLoginListener(PlayerPreLoginEvent e) {
        NPCPlayer player = NPCPlayer.getNPCPlayer(e.getName());
        if (player != null) {
            player.removePlayer();
        }
    }
}
