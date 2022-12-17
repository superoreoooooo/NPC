package xyz.oreodev.npc;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.oreodev.npc.command.account.accountCompleter;
import xyz.oreodev.npc.command.item.itemCompleter;
import xyz.oreodev.npc.command.npc.NPCCommand;
import xyz.oreodev.npc.command.npc.NPCCompleter;
import xyz.oreodev.npc.command.account.accountCommand;
import xyz.oreodev.npc.command.item.itemCommand;
import xyz.oreodev.npc.listener.DeathListener;
import xyz.oreodev.npc.listener.PreLoginListener;
import xyz.oreodev.npc.listener.accountListener;
import xyz.oreodev.npc.listener.playerMovementListener;
import xyz.oreodev.npc.listener.shop.shopListener;
import xyz.oreodev.npc.manager.accountYmlManager;
import xyz.oreodev.npc.manager.npcYmlManager;
import xyz.oreodev.npc.manager.priceDataYmlManager;
import xyz.oreodev.npc.manager.shopYmlManager;
import xyz.oreodev.npc.util.account.account;
import xyz.oreodev.npc.util.npc.Color;
import xyz.oreodev.npc.util.npc.NPCPlayer;
import xyz.oreodev.npc.util.shop.shopUtil;
import xyz.oreodev.npc.version.Version;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Main extends JavaPlugin {
    private static Main plugin;

    public FileConfiguration config;
    public accountYmlManager accountYmlManager;
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
        getCommand("account").setExecutor(new accountCommand());
        getCommand("account").setTabCompleter(new accountCompleter());
        getCommand("item").setExecutor(new itemCommand());
        getCommand("item").setTabCompleter(new itemCompleter());

        getServer().getPluginManager().registerEvents(new DeathListener(), this);
        getServer().getPluginManager().registerEvents(new PreLoginListener(), this);
        getServer().getPluginManager().registerEvents(new playerMovementListener(), this);
        getServer().getPluginManager().registerEvents(new shopListener(), this);
        getServer().getPluginManager().registerEvents(new accountListener(), this);

        plugin = this;

        this.acc = new account();

        checkForClasses();

        this.saveDefaultConfig();
        config = this.getConfig();
        this.ymlManager = new npcYmlManager(this);
        this.shopYmlManager = new shopYmlManager(this);
        this.accountYmlManager = new accountYmlManager(this);
        this.priceDataYmlManager = new priceDataYmlManager(this);

        initializeAccount();
        initializeNPC();
        initializeShop();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().setCollidesWithEntities(false);
        }
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
        String[] args = new String[2];
        for (String uuid : plugin.ymlManager.getConfig().getConfigurationSection("npc.").getKeys(false)) {
            if (NPCPlayer.summon(plugin.ymlManager.getConfig().getString("npc." + uuid + ".name"),
                    plugin.ymlManager.getConfig().getDouble("npc." + uuid + ".locX"),
                    plugin.ymlManager.getConfig().getDouble("npc." + uuid + ".locY"),
                    plugin.ymlManager.getConfig().getDouble("npc." + uuid + ".locZ"),
                    UUID.fromString(uuid),
                    plugin.ymlManager.getConfig().getString("npc." + uuid + ".skin"))) {
                args[0] = plugin.ymlManager.getConfig().getString("npc." + uuid + ".name");
                args[1] = uuid;
                Bukkit.getConsoleSender().sendMessage( getConfigMessage(config, "messages.npc.npc-load", args));
            }
        }
    }

    public void initializeAccount() {
        String[] args = new String[2];
        for (String name : this.accountYmlManager.getConfig().getConfigurationSection("account.").getKeys(false)) {
            account.accountMap.put(name, acc.getBalance(name));
            args[0] = name;
            args[1] = String.valueOf(acc.getBalance(name));
            Bukkit.getConsoleSender().sendMessage(getConfigMessage(config, "messages.account.load", args));
        }
    }

    public void initializeShop() {
        shopUtil util = new shopUtil();
        String[] args = new String[2];
        for (String uuid : plugin.shopYmlManager.getConfig().getConfigurationSection("shop.").getKeys(false)) {
            shopUtil.shopMap.put(UUID.fromString(uuid), util.getSavedTitle(UUID.fromString(uuid)));
            args[0] = shopUtil.shopMap.get(UUID.fromString(uuid));
            args[1] = uuid;
            Bukkit.getConsoleSender().sendMessage(getConfigMessage(config, "messages.npc.shop-load", args));
        }
    }

    public static UUID getRandomUUID(String name) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
        return offlinePlayer.getUniqueId();
    }

    public void saveNPC() {
        String[] args = new String[2];
        for (EntityPlayer entityPlayer : Main.npcs) {
            for (NPCPlayer npcPlayer : NPCPlayer.npcPlayerList) {
                if (npcPlayer.getEntityPlayer().equals(entityPlayer)) {
                    plugin.ymlManager.getConfig().set("npc." + npcPlayer.getUUID().toString() + ".name", npcPlayer.getName());
                    plugin.ymlManager.getConfig().set("npc." + npcPlayer.getUUID().toString() + ".locX", npcPlayer.getEntityPlayer().getBukkitEntity().getLocation().getX());
                    plugin.ymlManager.getConfig().set("npc." + npcPlayer.getUUID().toString() + ".locY", npcPlayer.getEntityPlayer().getBukkitEntity().getLocation().getY());
                    plugin.ymlManager.getConfig().set("npc." + npcPlayer.getUUID().toString() + ".locZ", npcPlayer.getEntityPlayer().getBukkitEntity().getLocation().getZ());
                    plugin.ymlManager.getConfig().set("npc." + npcPlayer.getUUID().toString() + ".skin", npcPlayer.getSkinName());
                    plugin.ymlManager.saveConfig();
                    args[0] = npcPlayer.getName();
                    args[1] = npcPlayer.getUUID().toString();
                    Bukkit.getConsoleSender().sendMessage( getConfigMessage(config, "messages.npc.npc-save", args));
                }
            }
        }
    }

    public static String getConfigMessage(FileConfiguration config, String path, String[] args) {
        String text = config.getString(path);
        if (text == null) {
            return ChatColor.RED +"ERROR";
        }

        boolean open = false;
        StringBuilder chars = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c == '%') {
                if (open) {
                    final char[] CHARACTERS = chars.toString().toCharArray();
                    if (CHARACTERS[0] == 'a' && CHARACTERS[1] == 'r' && CHARACTERS[2] == 'g') {
                        final int ARG = Integer.parseInt(String.valueOf(CHARACTERS[3]));

                        text = text.replace(chars.toString(), args[ARG]);

                        chars = new StringBuilder();
                    }
                    open = false;
                } else {
                    open = true;
                }
                continue;
            }

            if (open) {
                chars.append(c);
            }
        }

        return Color.format(config.getString("prefix") + " " + text.replace("%", ""));
    }
}
