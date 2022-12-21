package win.oreo.npc.listener.shop;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.java.JavaPlugin;
import win.oreo.npc.Main;
import win.oreo.npc.command.npc.NPCCommand;
import win.oreo.npc.util.item.itemUtil;
import win.oreo.npc.util.shop.shopExchange;
import win.oreo.npc.util.shop.shopUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


public class shopListener implements Listener {
    private shopUtil util;
    private shopExchange shopExchange;

    List<Player> coolDown = new ArrayList<>();

    public shopListener() {
        this.shopExchange = new shopExchange();
        this.util = new shopUtil();
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
        if (e.getRightClicked().getType().equals(EntityType.PLAYER)) {
            if (e.getRightClicked().hasMetadata("npc")) {
                String str = e.getRightClicked().getMetadata("npc").get(0).asString();
                util.openShop(player, str);
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (NPCCommand.editorList.contains((Player)e.getWhoClicked())) return;
        if (e.getClickedInventory() == null) {
            return;
        }
        if (e.getClickedInventory().getTitle().contains("_상점")) {
            e.setCancelled(true);
            if (coolDown.contains((Player)e.getWhoClicked())) return;
            delay((Player)e.getWhoClicked());
            shopExchange.buy((Player)e.getWhoClicked(), e.getCurrentItem());
        }
        else if (e.getInventory().getTitle().contains("_상점")) {
            e.setCancelled(true);
            if (coolDown.contains((Player)e.getWhoClicked())) return;
            delay((Player)e.getWhoClicked());
            shopExchange.sell((Player) e.getWhoClicked(), e.getCurrentItem());
        }
        //e.getWhoClicked().sendMessage(e.getClickedInventory().getName() + " // " + e.getClick().toString() + " // " + e.getCurrentItem().getType().toString() + " // " + e.getCursor().getType().toString() + " // " + e.getAction().toString());
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (NPCCommand.editorList.contains((Player)e.getWhoClicked())) return;
        if (e.getInventory().getTitle().contains("_상점")) {
            if (util.getSavedInventorySize(util.getIDFromName(e.getInventory().getTitle().split("_")[0])) > Collections.min(e.getRawSlots())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onGet(PlayerPickupItemEvent e) {
        for (EntityPlayer entityPlayer : Main.npcs) {
            if (e.getPlayer().equals(entityPlayer.getBukkitEntity().getPlayer())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!NPCCommand.editorList.contains((Player)e.getPlayer())) return;
        NPCCommand.editorList.remove((Player)e.getPlayer());
        if (e.getInventory().getTitle().contains("_상점")) {
            String[] storeName = e.getInventory().getTitle().split("_");
            int cnt = 0;
            List<String> items = new ArrayList<>();
            for (String str : shopUtil.shopMap.values()) {
                if (str.equalsIgnoreCase(storeName[0])) {
                    for (UUID uuid : shopUtil.shopMap.keySet()) {
                        if (shopUtil.shopMap.get(uuid).equalsIgnoreCase(storeName[0])) {
                            for (int i = 0; i < e.getInventory().getSize(); i++) {
                                util.saveItemStack(uuid, i, e.getInventory().getItem(i));
                                if (e.getInventory().getItem(i) != null) {
                                    items.add(e.getInventory().getItem(i).getType().name());
                                    cnt++;
                                }
                            }
                        }
                    }
                }
            }
            e.getPlayer().sendMessage("Saved items : (" + cnt + ")" + items);
        }
    }
}