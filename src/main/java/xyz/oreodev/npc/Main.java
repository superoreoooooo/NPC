package xyz.oreodev.npc;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.oreodev.npc.command.acc.accCompleter;
import xyz.oreodev.npc.command.item.itemCompleter;
import xyz.oreodev.npc.command.npc.NPCCommand;
import xyz.oreodev.npc.command.npc.NPCCompleter;
import xyz.oreodev.npc.command.acc.accCommand;
import xyz.oreodev.npc.command.item.itemCommand;
import xyz.oreodev.npc.listener.DeathListener;
import xyz.oreodev.npc.listener.PreLoginListener;
import xyz.oreodev.npc.listener.accListener;
import xyz.oreodev.npc.listener.playerMovementListener;
import xyz.oreodev.npc.listener.shop.shopListener;
import xyz.oreodev.npc.manager.accYmlManager;
import xyz.oreodev.npc.manager.npcYmlManager;
import xyz.oreodev.npc.manager.priceDataYmlManager;
import xyz.oreodev.npc.manager.shopYmlManager;
import xyz.oreodev.npc.util.acc.account;
import xyz.oreodev.npc.util.npc.NPCPlayer;
import xyz.oreodev.npc.util.shop.shopUtil;
import xyz.oreodev.npc.version.Version;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Main extends JavaPlugin {
    private static Main plugin;
    public FileConfiguration config;
    public accYmlManager moneyConfig;
    public npcYmlManager ymlManager;
    public shopYmlManager shopYmlManager;
    public priceDataYmlManager priceDataYmlManager;

    private boolean usesPaper = false;
    private boolean updatedPaper = false;

    private account acc;
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

        getCommand("npc").setExecutor(new NPCCommand());
        getCommand("npc").setTabCompleter(new NPCCompleter());
        getCommand("account").setExecutor(new accCommand());
        getCommand("account").setTabCompleter(new accCompleter());
        getCommand("item").setExecutor(new itemCommand());
        getCommand("item").setTabCompleter(new itemCompleter());

        getServer().getPluginManager().registerEvents(new DeathListener(), this);
        getServer().getPluginManager().registerEvents(new PreLoginListener(), this);
        getServer().getPluginManager().registerEvents(new playerMovementListener(), this);
        getServer().getPluginManager().registerEvents(new shopListener(), this);
        getServer().getPluginManager().registerEvents(new accListener(), this);

        plugin = this;

        this.acc = new account();

        checkForClasses();

        this.ymlManager = new npcYmlManager(this);
        this.shopYmlManager = new shopYmlManager(this);
        this.moneyConfig = new accYmlManager(this);
        this.priceDataYmlManager = new priceDataYmlManager(this);

        initializeAccount();
        initializeNPC();
        initializeShop();
    }

    @Override
    public void onDisable() {
        saveNPC();
        List<NPCPlayer> list = new ArrayList<>(NPCPlayer.getNPCPlayerList());
        for (NPCPlayer player : list) {
            player.removePlayer();
        }
    }

    public void initializeNPC() {
        loadNPC();
    }

    public void initializeAccount() {
        Bukkit.getConsoleSender().sendMessage("========================================");
        for (String name : this.moneyConfig.getConfig().getConfigurationSection("account.").getKeys(false)) {
            account.accountMap.put(name, acc.getBalance(name));
            Bukkit.getConsoleSender().sendMessage("account : " + name + " | balance : " + acc.getBalance(name));
        }
        Bukkit.getConsoleSender().sendMessage("========================================");
    }

    public void initializeShop() {
        shopUtil util = new shopUtil();
        for (String uuid : plugin.shopYmlManager.getConfig().getConfigurationSection("shop.").getKeys(false)) {
            shopUtil.shopMap.put(UUID.fromString(uuid), util.getSavedTitle(UUID.fromString(uuid)));
        }
        for (UUID uuid : shopUtil.shopMap.keySet()) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Loaded shop / uuid : " + uuid + " name : " + shopUtil.shopMap.get(uuid));
        }
    }

    public static UUID getRandomUUID(String name) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
        return offlinePlayer.getUniqueId();
    }

    public void saveNPC() {
        for (EntityPlayer entityPlayer : Main.npcs) {
            for (NPCPlayer npcPlayer : NPCPlayer.npcPlayerList) {
                if (npcPlayer.getEntityPlayer().equals(entityPlayer)) {
                    plugin.ymlManager.getConfig().set("npc." + npcPlayer.getUUID().toString() + ".name", npcPlayer.getName());
                    plugin.ymlManager.getConfig().set("npc." + npcPlayer.getUUID().toString() + ".locX", npcPlayer.getEntityPlayer().getBukkitEntity().getLocation().getX());
                    plugin.ymlManager.getConfig().set("npc." + npcPlayer.getUUID().toString() + ".locY", npcPlayer.getEntityPlayer().getBukkitEntity().getLocation().getY());
                    plugin.ymlManager.getConfig().set("npc." + npcPlayer.getUUID().toString() + ".locZ", npcPlayer.getEntityPlayer().getBukkitEntity().getLocation().getZ());
                    plugin.ymlManager.getConfig().set("npc." + npcPlayer.getUUID().toString() + ".skin", npcPlayer.getSkinName());
                    plugin.ymlManager.saveConfig();
                    Bukkit.getConsoleSender().sendMessage( "(NPC) uuid : " + npcPlayer.getUUID().toString() + " name : " + npcPlayer.getName() + " saved!");
                }
            }
        }
    }

    public void loadNPC() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "test");
        for (String uuid : plugin.ymlManager.getConfig().getConfigurationSection("npc.").getKeys(false)) {
            if (NPCPlayer.summon(plugin.ymlManager.getConfig().getString("npc." + uuid + ".name"), plugin.ymlManager.getConfig().getDouble("npc." + uuid + ".locX"), plugin.ymlManager.getConfig().getDouble("npc." + uuid + ".locY"), plugin.ymlManager.getConfig().getDouble("npc." + uuid + ".locZ"), UUID.fromString(uuid), plugin.ymlManager.getConfig().getString("npc." + uuid + ".skin"))) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Loaded NPC : " + plugin.ymlManager.getConfig().getString("npc." + uuid + ".name"));
            }
        }
    }
}
