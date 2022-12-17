package xyz.oreodev.npc.listener;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.oreodev.npc.Main;
import xyz.oreodev.npc.util.account.account;

public class accountListener implements Listener {
    private xyz.oreodev.npc.util.account.account account;

    public accountListener() {
        account = new account();
    }

    @EventHandler
    public void onFirstJoin(PlayerJoinEvent e) {
        if (!e.getPlayer().hasPlayedBefore()) {
            account.setBalance(e.getPlayer().getName(), 0);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(Main.class), () -> {
            EntityPlayer entityPlayer = ((CraftPlayer) e.getPlayer()).getHandle();
            if (Main.npcs.contains(entityPlayer)) {
                account.removeAccount(e.getPlayer().getName());
            }
        }, 100); //5초
    }
}
