package xyz.oreodev.npc.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.oreodev.npc.util.acc.account;

public class accListener implements Listener {
    private xyz.oreodev.npc.util.acc.account account;

    public accListener() {
        account = new account();
    }
    @EventHandler
    public void onFirstJoin(PlayerJoinEvent e) {
        if (!e.getPlayer().hasPlayedBefore()) {
            account.setBalance(e.getPlayer().getName(), 0);
        }
    }
}
