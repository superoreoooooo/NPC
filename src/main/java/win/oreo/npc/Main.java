package win.oreo.npc;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import win.oreo.npc.command.quest.description.questDescriptionCommand;
import win.oreo.npc.command.quest.description.questDescriptionCompleter;
import win.oreo.npc.command.quest.questCommand;
import win.oreo.npc.command.quest.questCompleter;
import win.oreo.npc.command.questNpc.questNpcCommand;
import win.oreo.npc.command.questNpc.questNpcCompleter;
import win.oreo.npc.command.questPlayer.questPlayerCommand;
import win.oreo.npc.command.questPlayer.questPlayerCompleter;
import win.oreo.npc.listener.account.accountListener;
import win.oreo.npc.listener.quest.questNpcListener;
import win.oreo.npc.listener.shop.shopListener;
import win.oreo.npc.manager.*;
import win.oreo.npc.util.account.account;
import win.oreo.npc.util.item.CustomEnchantment;
import win.oreo.npc.util.item.itemUtil;
import win.oreo.npc.util.npc.NPCPlayer;
import win.oreo.npc.command.account.accountCompleter;
import win.oreo.npc.command.item.itemCompleter;
import win.oreo.npc.command.npc.NPCCommand;
import win.oreo.npc.command.npc.NPCCompleter;
import win.oreo.npc.command.account.accountCommand;
import win.oreo.npc.command.item.itemCommand;
import win.oreo.npc.listener.npc.DeathListener;
import win.oreo.npc.listener.npc.PreLoginListener;
import win.oreo.npc.listener.npc.playerMovementListener;
import win.oreo.npc.util.npc.Color;
import win.oreo.npc.util.quest.Quest;
import win.oreo.npc.util.quest.description.QuestDescriptionUtil;
import win.oreo.npc.util.quest.npc.QuestNpc;
import win.oreo.npc.util.quest.npc.QuestNpcUtil;
import win.oreo.npc.util.quest.player.QuestPlayer;
import win.oreo.npc.util.quest.player.QuestPlayerUtil;
import win.oreo.npc.util.quest.questType;
import win.oreo.npc.util.quest.QuestUtil;
import win.oreo.npc.util.shop.shopUtil;
import win.oreo.npc.version.Version;

import java.util.*;

public final class Main extends JavaPlugin {
    private static Main plugin;

    public FileConfiguration config;
    public win.oreo.npc.manager.accountYmlManager accountYmlManager;
    public npcYmlManager ymlManager;
    public win.oreo.npc.manager.shopYmlManager shopYmlManager;
    public win.oreo.npc.manager.priceDataYmlManager priceDataYmlManager;
    public win.oreo.npc.manager.questYml questYml;

    private boolean usesPaper = false;
    private boolean updatedPaper = false;

    private account acc;
    private itemUtil itemUtil;

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
        getCommand("quest").setExecutor(new questCommand());
        getCommand("quest").setTabCompleter(new questCompleter());
        getCommand("questnpc").setExecutor(new questNpcCommand());
        getCommand("questnpc").setTabCompleter(new questNpcCompleter());
        getCommand("questplayer").setExecutor(new questPlayerCommand());
        getCommand("questplayer").setTabCompleter(new questPlayerCompleter());
        getCommand("questdescription").setExecutor(new questDescriptionCommand());
        getCommand("questdescription").setTabCompleter(new questDescriptionCompleter());

        getServer().getPluginManager().registerEvents(new DeathListener(), this);
        getServer().getPluginManager().registerEvents(new PreLoginListener(), this);
        getServer().getPluginManager().registerEvents(new playerMovementListener(), this);
        getServer().getPluginManager().registerEvents(new shopListener(), this);
        getServer().getPluginManager().registerEvents(new accountListener(), this);
        getServer().getPluginManager().registerEvents(new questNpcListener(), this);

        plugin = this;

        this.acc = new account();
        this.itemUtil = new itemUtil();

        checkForClasses();

        this.saveDefaultConfig();
        config = this.getConfig();
        this.ymlManager = new npcYmlManager(this);
        this.shopYmlManager = new shopYmlManager(this);
        this.accountYmlManager = new accountYmlManager(this);
        this.priceDataYmlManager = new priceDataYmlManager(this);
        this.questYml = new questYml(this);

        initializeAccount();
        initializeShop();
        initializeItem();
        initializeQuest();
        initializeQuestNpc();
        initializeQuestPlayer();
        initializeQuestDescription();
        initializeNPC();

        CustomEnchantment.register();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().setCollidesWithEntities(false);
        }

        Bukkit.getConsoleSender().sendMessage("load complete!");
    }

    @Override
    public void onDisable() {
        saveNPC();
        itemUtil.savePriceData();
        QuestUtil questUtil = new QuestUtil();
        questUtil.saveAllQuest();
        QuestNpcUtil questNpcUtil = new QuestNpcUtil();
        questNpcUtil.saveAllQuestNpc();
        QuestPlayerUtil questPlayerUtil = new QuestPlayerUtil();
        questPlayerUtil.saveAllQuestPlayer();
        QuestDescriptionUtil questDescriptionUtil = new QuestDescriptionUtil();
        questDescriptionUtil.saveDescription();
        List<NPCPlayer> list = new ArrayList<>(NPCPlayer.getNPCPlayerList());
        for (NPCPlayer player : list) {
            player.removePlayer();
        }
    }

    public void initializeQuest() {
        String[] args = new String[2];
        for (String uuid : questYml.getConfig().getConfigurationSection("quest.").getKeys(false)) {

            UUID questID = UUID.fromString(uuid);
            String name = questYml.getConfig().getString("quest." + uuid + ".name");
            questType type = questType.valueOf(questYml.getConfig().getString("quest." + uuid + ".type"));
            Object target = questYml.getConfig().get("quest." + uuid + ".target");
            int goal = questYml.getConfig().getInt("quest." + uuid + ".goal");
            ItemStack reward = questYml.getConfig().getItemStack("quest." + uuid + ".reward");

            QuestUtil.questList.add(new Quest(questID, name, type, target, goal, reward));
            args[0] = name;
            args[1] = questID.toString();
            Bukkit.getConsoleSender().sendMessage(getConfigMessage(config, "messages.quest.load", args));
        }
    }

    public void initializeQuestNpc() {
        QuestUtil questUtil = new QuestUtil();
        String[] args = new String[2];
        for (String name : questYml.getConfig().getConfigurationSection("npc.").getKeys(false)) {
            HashMap<Integer, Quest> map = new HashMap<>();
            for (int i = 0; i < questYml.getConfig().getInt("npc." + name + ".count"); i++) {
                UUID uuid = UUID.fromString(questYml.getConfig().getString("npc." + name + ".list." + i));
                map.put(i, questUtil.getQuestByID(uuid));
            }
            QuestNpc npc = new QuestNpc(name, map);
            QuestNpcUtil.questNpcList.add(npc);
            args[0] = npc.getNpcName();
            List<String> list = new ArrayList<>();
            npc.getQuestMap().values().forEach(quest -> list.add(quest.getQuestID().toString()));
            args[1] = list.toString();
            Bukkit.getConsoleSender().sendMessage(getConfigMessage(config, "messages.quest.npc.load", args));
        }
    }

    public void initializeQuestPlayer() {
        String[] args = new String[1];
        for (String playerName : questYml.getConfig().getConfigurationSection("player.").getKeys(false)) {
            HashMap<String, Integer> map = new HashMap<>();
            HashMap<String, Integer> map2 = new HashMap<>();
            Set<String> com = new HashSet<>();
            for (String npcName : questYml.getConfig().getConfigurationSection("player." + playerName + ".npc.").getKeys(false)) {
                if (questYml.getConfig().getBoolean("player." + playerName + ".npc." + npcName + ".complete")) com.add(npcName);
                int i = questYml.getConfig().getInt("player." + playerName + ".npc." + npcName + ".proceeding");
                int j = questYml.getConfig().getInt("player." + playerName + ".npc." + npcName + ".progress");
                map.put(npcName, i);
                map2.put(npcName, j);
            }
            QuestPlayer questPlayer = new QuestPlayer(Bukkit.getOfflinePlayer(playerName), map, com, map2);
            QuestPlayerUtil.questPlayerList.add(questPlayer);
            args[0] = playerName;
            Bukkit.getConsoleSender().sendMessage(getConfigMessage(config, "messages.quest.player.load", args));
        }
    }

    public void initializeQuestDescription() {
        String[] args = new String[2];
        for (String uuid : questYml.getConfig().getConfigurationSection("description.").getKeys(false)) {
            QuestUtil util = new QuestUtil();
            Quest quest = util.getQuestByID(UUID.fromString(uuid));
            List<String> strings = questYml.getConfig().getStringList("description." + uuid);
            QuestDescriptionUtil.descriptionMap.put(quest, strings);
            args[0] = quest.getQuestID().toString();
            args[1] = Arrays.toString(strings.toArray());
            Bukkit.getConsoleSender().sendMessage(getConfigMessage(config, "messages.quest.description.load", args));
        }
    }

    public void initializeItem() {
        String[] str = new String[4];
        for (String name : plugin.priceDataYmlManager.getConfig().getConfigurationSection("item.").getKeys(false)) {
            if (plugin.priceDataYmlManager.getConfig().getItemStack("item." + name + ".itemStack").getType().equals(Material.AIR)) continue;
            win.oreo.npc.util.item.itemUtil.priceMap.put(plugin.priceDataYmlManager.getConfig().getItemStack("item." + name + ".itemStack"), plugin.priceDataYmlManager.getConfig().getInt("item." + name + ".price"));
        }
        for (ItemStack itemStack : win.oreo.npc.util.item.itemUtil.priceMap.keySet()) {
            str[0] = win.oreo.npc.util.item.itemUtil.getItemName(itemStack);
            str[1] = itemStack.getType().name();
            str[2] = String.valueOf(win.oreo.npc.util.item.itemUtil.priceMap.get(itemStack));
            str[3] = String.valueOf(itemStack.getAmount());
            Bukkit.getConsoleSender().sendMessage( getConfigMessage(config, "messages.item.load", str));
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
        for (EntityPlayer player : Main.npcs) {
            if (player.getBukkitEntity().isDead()) {
                player.getBukkitEntity().spigot().respawn();
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
        String prefix = config.getString("prefix");
        if (text == null) {
            return ChatColor.RED +"ERROR";
        }
        if (path.contains("account")) {
            prefix = config.getString("prefixAcc");
        } else if (path.contains("quest")) {
            prefix = config.getString("prefixQuest");
        } else if (path.contains("npc")) {
            prefix = config.getString("prefixNpc");
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

        return Color.format(prefix + " " + text.replace("%", ""));
    }
}
