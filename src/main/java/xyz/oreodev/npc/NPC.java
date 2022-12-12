package xyz.oreodev.npc;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.oreodev.npc.command.NPCPlayers;
import xyz.oreodev.npc.command.NPCPlayersCompleter;
import xyz.oreodev.npc.listener.DeathListener;
import xyz.oreodev.npc.listener.PreLoginListener;
import xyz.oreodev.npc.util.Color;
import xyz.oreodev.npc.version.Version;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class NPC extends JavaPlugin {
    private static NPC plugin;

    public FileConfiguration config;

    private boolean usesCraftBukkit = false;
    private boolean usesPaper = false;
    private boolean updatedPaper = false;

    private Version version = Version.valueOf(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3]);

    public static NPC getPlugin() {
        return plugin;
    }

    public static String getConfigMessage(FileConfiguration config, String path, String[] args) {
        String text = config.getString(path);

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
        File cacheFolder = new File("plugins/NPC/cache");
        if (!cacheFolder.exists()) {
            cacheFolder.mkdir();
        }

        if (version == null) {
            Bukkit.getLogger().warning("ERROR! NOT SUPPORTED VERSION!");
        }

        Bukkit.getLogger().info("Detected version : " + version.name());

        getCommand("npc").setExecutor(new NPCPlayers());
        getCommand("npc").setTabCompleter(new NPCPlayersCompleter());

        getServer().getPluginManager().registerEvents(new DeathListener(), this);
        getServer().getPluginManager().registerEvents(new PreLoginListener(), this);

        plugin = this;

        checkForClasses();

        this.saveDefaultConfig();
        config = this.getConfig();

        validateConfig();
    }

    public boolean usesCraftBukkit() {
        return usesCraftBukkit;
    }

    @Override
    public void onDisable() {
        List<NPCPlayer> copyList = new ArrayList<>(NPCPlayer.getNPCPlayers());
        try {
            BufferedWriter myWriter = new BufferedWriter(new FileWriter("plugins/NPC/cache/cache$1.fpcache"));
            for (NPCPlayer player : copyList) {
                myWriter.write(player.getName() + "\n");
                player.removePlayer();
            }
            myWriter.close();
        } catch (IOException e) {
            Bukkit.getLogger().warning("Failed to cache NPCs who are currently online. They will not rejoin your server.");
        }
    }

    public void validateConfig() {
        InputStream is = getResource("config.yml");
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(new InputStreamReader(is));
        Set<String> pluginKeys = configuration.getKeys(true);
        Set<String> configKeys = config.getKeys(true);

        for (String s : pluginKeys) {
            if (!configKeys.contains(s)) {
                System.out.println("You are using an invalid version of NPCs config. Creating a new one...");
                new File("plugins/NPC/config.yml").delete();
                this.saveDefaultConfig();
                return;
            }
        }
    }

    public static UUID getRandomUUID(String name) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
        return offlinePlayer.getUniqueId();
    }
}
