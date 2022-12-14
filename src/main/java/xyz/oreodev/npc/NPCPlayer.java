package xyz.oreodev.npc;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.oreodev.npc.version.v1_12_R1;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPCPlayer {

    public static List<NPCPlayer> npcPlayerList = new ArrayList<>();
    private UUID uuid;
    private String name;
    private EntityPlayer entityPlayer;
    private Main plugin;

    public NPCPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public NPCPlayer() {

    }

    public static NPCPlayer getNPCPlayer(UUID uuid) {
        for (NPCPlayer player : npcPlayerList) {
            if (player.getUUID() == uuid) {
                return player;
            }
        }
        return null;
    }

    public static NPCPlayer getNPCPlayer(String name) {
        for (NPCPlayer player : npcPlayerList) {
            if (player.getName().equals(name)) {
                return player;
            }
        }
        return null;
    }

    public static int getAmount() {
        return npcPlayerList.size();
    }

    public static List<NPCPlayer> getNPCPlayerList() {
        return npcPlayerList;
    }

    public static boolean summon(String name, double x, double y, double z, UUID uuid) {
        return new NPCPlayer(uuid, name).spawn(x, y, z);
    }

    public boolean spawn(double x, double y, double z) {
        if (name.length() >= 16) {
            return false;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return false;
            }
        }

        entityPlayer = v1_12_R1.spawn(this, x, y, z);
        npcPlayerList.add(this);

        return true;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public EntityPlayer getEntityPlayer() {
        return entityPlayer;
    }

    public void removePlayer() {
        v1_12_R1.removePlayer(this);
    }

    public void removeDataAndPlayer() {
        plugin = JavaPlugin.getPlugin(Main.class);
        plugin.ymlManager.getConfig().set("npc." + this.getUUID().toString(), null);
        plugin.ymlManager.saveConfig();
        v1_12_R1.removePlayer(this);
    }
}
