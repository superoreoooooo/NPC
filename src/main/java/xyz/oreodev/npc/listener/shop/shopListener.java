package xyz.oreodev.npc.listener.shop;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.oreodev.npc.Main;
import xyz.oreodev.npc.command.shop.shopCommand;
import xyz.oreodev.npc.util.shop.shopUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class shopListener implements Listener {
    private shopUtil util;

    List<Player> coolDown = new ArrayList<>();

    public shopListener() {
        this.util = new shopUtil();
    }

    public void delay(Player player) {
        coolDown.add(player);
        Bukkit.getScheduler().runTaskLaterAsynchronously(JavaPlugin.getPlugin(Main.class), () -> coolDown.remove(player), 20);
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent e) {
        Player player = e.getPlayer();
        if (coolDown.contains(player)) return;
        delay(player);
        player.setCooldown(player.getInventory().getItemInMainHand().getType(), 20);
        if (e.getRightClicked().getType().equals(EntityType.PLAYER)) {
            if (e.getRightClicked().hasMetadata("npc")) {
                String str = e.getRightClicked().getMetadata("npc").get(0).asString();
                player.sendMessage(str);
                util.openShop(player, str);
            }
        }
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent e) {
        if (shopCommand.editorList.contains((Player) e.getPlayer())) return;
        if (e.getInventory().getTitle().contains("_상점")) {
            e.getPlayer().sendMessage("opened shop : " + e.getInventory().getTitle().split("_")[0]);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (shopCommand.editorList.contains((Player)e.getWhoClicked())) return;
        if (e.getClickedInventory() == null) return;
        //currentItem :
        e.getWhoClicked().sendMessage(e.getClickedInventory().getName() + " // " + e.getClick().toString() + " // " + e.getCurrentItem().getType().toString() + " // " + e.getCursor().getType().toString() + " // " + e.getAction().toString());
    }
    //todo fix / test inventory

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getInventory().getTitle().contains("_상점")) {
            String[] storeName = e.getInventory().getTitle().split("_");
            e.getPlayer().sendMessage("closed shop : " + storeName[0]);
            if (shopCommand.editorList.contains((Player)e.getPlayer())) {
                e.getPlayer().sendMessage("Saved items");
                for (String str : shopUtil.shopMap.values()) {
                    if (str.equalsIgnoreCase(storeName[0])) {
                        for (UUID uuid : shopUtil.shopMap.keySet()) {
                            if (shopUtil.shopMap.get(uuid).equalsIgnoreCase(storeName[0])) {
                                for (int i = 0; i < e.getInventory().getSize(); i++) {
                                    util.saveItemStack(uuid, i, e.getInventory().getItem(i));
                                    if (e.getInventory().getItem(i) != null) {
                                        e.getPlayer().sendMessage(e.getInventory().getItem(i).getType().name());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        shopCommand.editorList.remove((Player)e.getPlayer());
    }
}