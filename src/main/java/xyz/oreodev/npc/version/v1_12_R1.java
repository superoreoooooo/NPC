package xyz.oreodev.npc.version;

import com.mojang.authlib.GameProfile;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;
import xyz.oreodev.npc.NPCPlayer;
import xyz.oreodev.npc.Main;
import xyz.oreodev.npc.paper.PaperUtils_v1_12_R1;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.UUID;

public class v1_12_R1 {
    public static EntityPlayer spawn(NPCPlayer NPCPlayer, double x, double y, double z, float yaw, float pitch) {
        WorldServer worldServer = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
        MinecraftServer mcServer = ((CraftServer) Bukkit.getServer()).getServer();
        EntityPlayer entityPlayer = createEntityPlayer(NPCPlayer.getUUID(), NPCPlayer.getName(), worldServer);
        CraftPlayer bukkitPlayer = entityPlayer.getBukkitEntity();

        try {
            PlayerPreLoginEvent playerPreLoginEvent = new PlayerPreLoginEvent(NPCPlayer.getName(), InetAddress.getByName("127.0.0.1"), NPCPlayer.getUUID());
            AsyncPlayerPreLoginEvent asyncPlayerPreLoginEvent = new AsyncPlayerPreLoginEvent(NPCPlayer.getName(), InetAddress.getByName("127.0.0.1"), NPCPlayer.getUUID());
            new Thread(() -> Bukkit.getPluginManager().callEvent(asyncPlayerPreLoginEvent)).start();
            Bukkit.getPluginManager().callEvent(playerPreLoginEvent);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        mcServer.getPlayerList().a(entityPlayer);
        Location loc = bukkitPlayer.getLocation();
        entityPlayer.setPosition(x,y,z);

        entityPlayer.setPositionRotation(x, y, z, yaw/360 * 256, pitch);

        DataWatcher data = entityPlayer.getDataWatcher();
        data.set(DataWatcherRegistry.a.a(13), (byte)127);

        String joinMessage = getJoinMessage(entityPlayer);

        if (Main.getPlugin().usesPaper()) {
            PaperUtils_v1_12_R1.playerInitialSpawnEvent(bukkitPlayer);
        }

        entityPlayer.spawnIn(worldServer);
        entityPlayer.playerInteractManager.a((WorldServer) entityPlayer.world);
        entityPlayer.playerInteractManager.b(EnumGamemode.CREATIVE);
        entityPlayer.playerConnection = new PlayerConnection(mcServer, new NetworkManager(EnumProtocolDirection.SERVERBOUND), entityPlayer);
        entityPlayer.playerConnection.networkManager.channel = new EmbeddedChannel(new ChannelInboundHandlerAdapter());
        entityPlayer.playerConnection.networkManager.channel.close();

        worldServer.getPlayerChunkMap().addPlayer(entityPlayer);
        mcServer.getPlayerList().players.add(entityPlayer);

        try {
            Field j = PlayerList.class.getDeclaredField("j");
            j.setAccessible(true);
            Object valJ = j.get(mcServer.getPlayerList());

            Method jPut = valJ.getClass().getDeclaredMethod("put", Object.class, Object.class);
            jPut.invoke(valJ, bukkitPlayer.getUniqueId(), entityPlayer);

            Field playersByName = PlayerList.class.getDeclaredField("playersByName");
            playersByName.setAccessible(true);
            Object valPlayerByName = playersByName.get(mcServer.getPlayerList());

            Method playersByNamePut = Map.class.getDeclaredMethod("put", Object.class, Object.class);
            playersByNamePut.invoke(valPlayerByName, entityPlayer.getName(), entityPlayer);

        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }

        PlayerJoinEvent playerJoinEvent;
        playerJoinEvent = new PlayerJoinEvent(((CraftServer)Bukkit.getServer()).getPlayer(entityPlayer), joinMessage);
        Bukkit.getPluginManager().callEvent(playerJoinEvent);

        String finalJoinMessage = playerJoinEvent.getJoinMessage();

        if (finalJoinMessage != null && !finalJoinMessage.equals("")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(finalJoinMessage);
            }
        }

        PlayerResourcePackStatusEvent resourcePackStatusEventAccepted = new PlayerResourcePackStatusEvent(bukkitPlayer, PlayerResourcePackStatusEvent.Status.ACCEPTED);
        PlayerResourcePackStatusEvent resourcePackStatusEventSuccessFullyLoaded = new PlayerResourcePackStatusEvent(bukkitPlayer, PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED);

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> Bukkit.getPluginManager().callEvent(resourcePackStatusEventAccepted), 20);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> Bukkit.getPluginManager().callEvent(resourcePackStatusEventSuccessFullyLoaded), 40);

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(entityPlayer));
        }

        worldServer.addEntity(entityPlayer);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), entityPlayer::playerTick, 1, 1);

        Main.npcs.add(entityPlayer);

        return entityPlayer;
    }

    public static EntityPlayer createEntityPlayer(UUID uuid, String name, WorldServer worldServer) {
        MinecraftServer mcServer = ((CraftServer) Bukkit.getServer()).getServer();
        GameProfile gameProfile = new GameProfile(uuid, name);

        return new EntityPlayer(mcServer, worldServer, gameProfile, new PlayerInteractManager(worldServer));
    }

    public static void removePlayer(NPCPlayer player) {
        MinecraftServer mcServer = ((CraftServer) Bukkit.getServer()).getServer();
        CraftServer craftServer = (CraftServer) Bukkit.getServer();
        EntityPlayer entityPlayer = (EntityPlayer) player.getEntityPlayer();
        WorldServer worldServer = entityPlayer.getWorld().getWorld().getHandle();

        if (entityPlayer.activeContainer != entityPlayer.defaultContainer) {
            entityPlayer.closeInventory();
        }

        PlayerQuitEvent playerQuitEvent = new PlayerQuitEvent(craftServer.getPlayer(entityPlayer), entityPlayer.getName() + " left the game(NPC)");

        Bukkit.getPluginManager().callEvent(playerQuitEvent);

        Main.npcs.remove(entityPlayer);

        worldServer.getPlayerChunkMap().removePlayer(entityPlayer);
        worldServer.removeEntity(entityPlayer);


        if (mcServer.isMainThread()) {
            entityPlayer.playerTick();
        }

        if (!entityPlayer.inventory.getCarried().isEmpty()) {
            ItemStack carried = entityPlayer.inventory.getCarried();
            entityPlayer.drop(carried, false);
        }

        entityPlayer.getBukkitEntity().disconnect(playerQuitEvent.getQuitMessage());
        entityPlayer.getAdvancementData().a();
        mcServer.getPlayerList().players.remove(entityPlayer);

        try {
            Field j = PlayerList.class.getDeclaredField("j");
            j.setAccessible(true);
            Object valJ = j.get(mcServer.getPlayerList());

            Method jRemove = valJ.getClass().getDeclaredMethod("remove", Object.class);
            jRemove.invoke(valJ, entityPlayer.getUniqueID());
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }

        NPCPlayer.getNPCPlayers().remove(player);
        String finalQuitMessage = playerQuitEvent.getQuitMessage();

        if (finalQuitMessage != null && !finalQuitMessage.equals("")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
                connection.sendPacket(new PacketPlayOutEntityDestroy(entityPlayer.getId()));
                connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer));
                p.sendMessage(playerQuitEvent.getQuitMessage());
            }
        }

        try {
            Method savePlayerFile = PlayerList.class.getDeclaredMethod("savePlayerFile", EntityPlayer.class);
            savePlayerFile.setAccessible(true);
            savePlayerFile.invoke(mcServer.getPlayerList(), entityPlayer);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private static String getJoinMessage(EntityPlayer entityPlayer) {
        GameProfile gameProfile = entityPlayer.getProfile();
        UserCache userCache = ((CraftServer) Bukkit.getServer()).getServer().getUserCache();
        GameProfile gameProfile1 = userCache.getProfile(entityPlayer.getName());

        String str = gameProfile1 == null ? gameProfile.getName() : gameProfile1.getName();

        String joinMessage;
        if (entityPlayer.getProfile().getName().equalsIgnoreCase(str)) {
            joinMessage = LocaleI18n.a("multiplayer.player.joined", entityPlayer.getName());
        } else {
            joinMessage = LocaleI18n.a("multiplayer.player.joined.renamed", entityPlayer.getName(), str);
        }

        return joinMessage;
    }
}
