package xyz.oreodev.npc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import xyz.oreodev.npc.action.Action;
import xyz.oreodev.npc.action.ActionWait;
import xyz.oreodev.npc.version.v1_12_R1;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPCPlayer {

    private static List<NPCPlayer> npcPlayers = new ArrayList<>();
    public List<Action> actions = new ArrayList<>();
    private UUID uuid;
    private String name;
    private Object entityPlayer;

    public NPCPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public static NPCPlayer getNPCPlayer(UUID uuid) {
        for (NPCPlayer player : npcPlayers) {
            if (player.getUUID() == uuid) {
                return player;
            }
        }
        return null;
    }

    public static NPCPlayer getNPCPlayer(String name) {
        for (NPCPlayer player : npcPlayers) {
            if (player.getName().equals(name)) {
                return player;
            }
        }
        return null;
    }

    public static int getAmount() {
        return npcPlayers.size();
    }

    public static List<NPCPlayer> getNPCPlayers() {
        return npcPlayers;
    }

    public static boolean summon(String name) {
        return new NPCPlayer(NPC.getRandomUUID(name), name).spawn();
    }

    public boolean spawn() {
        List<HandlerList> copy = new ArrayList<>(HandlerList.getHandlerLists());
        if (name.length() >= 16) {
            return false;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return false;
            }
        }

        entityPlayer = v1_12_R1.spawn(this);
        npcPlayers.add(this);

        return true;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Object getEntityPlayer() {
        return entityPlayer;
    }

    public void removePlayer() {
        v1_12_R1.removePlayer(this);
    }

    public void addAction(Action action) {
        actions.add(action);
    }

    public void perform(int number) {
        for (int i = 0; i < number; i++) {
            for (Action action : actions) {
                if (!(action instanceof ActionWait)) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(NPC.getPlugin(), () -> action.perform(this), 0);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    action.perform(this);
                }
            }
        }
    }

    public List<Action> getActions() {
        return actions;
    }
}
