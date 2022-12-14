package xyz.oreodev.npc;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.oreodev.npc.command.NPC.NPCComand;
import xyz.oreodev.npc.command.NPC.NPCCompleter;
import xyz.oreodev.npc.listener.DeathListener;
import xyz.oreodev.npc.listener.PreLoginListener;
import xyz.oreodev.npc.listener.playerMovementListener;
import xyz.oreodev.npc.manager.npcYmlManager;
import xyz.oreodev.npc.manager.shopYmlManager;
import xyz.oreodev.npc.version.Version;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class Main extends JavaPlugin {
    private static Main plugin;
    public FileConfiguration config;
    public npcYmlManager ymlManager;
    public shopYmlManager shopYmlManager;

    private boolean usesCraftBukkit = false;
    private boolean usesPaper = false;
    private boolean updatedPaper = false;

    private NPCPlayer npcPlayer;

    public static List<EntityPlayer> npcs = new ArrayList<>();

    private Version version = Version.valueOf(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3]);

    public static Main getPlugin() {
        return plugin;
    }

    public boolean usesPaper() {
        return usesPaper;
    }

    public boolean isPaperUpdated() {
        return updatedPaper;
    }

    public Version getVersion() {
        return version;
    }

    public void checkForClasses() {
        try {
            usesPaper = Class.forName("com.destroystokyo.paper.VersionHistoryManager$VersionData") != null;
            if (usesPaper) {
                Bukkit.getLogger().info("Paper detected.");
            }
        } catch (ClassNotFoundException ignored) {

        }
        try {
            updatedPaper = Class.forName("net.kyori.adventure.text.ComponentLike") != null;
        } catch (ClassNotFoundException ignored) {

        }
    }

    @Override
    public void onEnable() {
        if (version == null) {
            Bukkit.getLogger().warning("ERROR! NOT SUPPORTED VERSION!");
        }

        Bukkit.getLogger().info("Detected version : " + version.name());

        getCommand("npc").setExecutor(new NPCComand());
        getCommand("npc").setTabCompleter(new NPCCompleter());

        getServer().getPluginManager().registerEvents(new DeathListener(), this);
        getServer().getPluginManager().registerEvents(new PreLoginListener(), this);
        getServer().getPluginManager().registerEvents(new playerMovementListener(), this);

        plugin = this;

        checkForClasses();

        this.saveDefaultConfig();
        config = this.getConfig();
        this.ymlManager = new npcYmlManager(this);
        this.shopYmlManager = new shopYmlManager(this);

        initialize();
    }

    public boolean usesCraftBukkit() {
        return usesCraftBukkit;
    }

    @Override
    public void onDisable() {
        npcPlayer = new NPCPlayer();
        npcPlayer.saveNPCPlayer();
    }

    public void initialize() {
        npcPlayer = new NPCPlayer();
        npcPlayer.loadNPCPlayers();
    }


    public static UUID getRandomUUID(String name) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
        return offlinePlayer.getUniqueId();
    }
}
