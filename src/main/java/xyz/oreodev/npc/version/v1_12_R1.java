package xyz.oreodev.npc.version;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
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
import org.bukkit.metadata.FixedMetadataValue;
import xyz.oreodev.npc.util.npc.NPCPlayer;
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
    public static EntityPlayer spawn(NPCPlayer NPCPlayer, double x, double y, double z) {
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

        entityPlayer.setPositionRotation(x, y, z, loc.getYaw(), loc.getPitch());

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
            connection.sendPacket(new PacketPlayOutEntityMetadata(entityPlayer.getId(), entityPlayer.getDataWatcher(), true));
        }

        worldServer.addEntity(entityPlayer);

        entityPlayer.getBukkitEntity().setMetadata("npc", new FixedMetadataValue(Main.getPlugin(), NPCPlayer.getName()));

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), entityPlayer::playerTick, 1, 1);

        Main.npcs.add(entityPlayer);

        return entityPlayer;
    }

    public static EntityPlayer createEntityPlayer(UUID uuid, String name, WorldServer worldServer) {
        MinecraftServer mcServer = ((CraftServer) Bukkit.getServer()).getServer();
        GameProfile gameProfile = new GameProfile(uuid, name);
        String signature = "uzkoA1xzJMxswMyk9+sDKKPUfm/5krGcxVGDpy1Ffx1Ondl3aMsx/2qLJRkNnXz9ZingNB3tyoajsvoOmJ1w9QTd4p7iWC+OXUgECyuHuxe1C78R5COBtQE5lurRvKW5PjFKec+6poWiJhWJa0mXVcRIu+2OJHC4V9crTjzR4HNA4uGx82XKWpK9p51iezp6uyf3aWqxZamlLNgZLix+38nvi9uQlSqUWUFhb7PPJiUgr7Kg2+GgDBJev6Zac9JK939O0ZOF8UaHiVFGB7kv+L2/Gcz6C3dZUtOuA0c+Ay4tB0LJYPa0I1k/nkIrs5GDSRVn7ux2902QiVMr3gG3zjszm5iLH58qCzJCuiNoTESgrhKA7fqgfQr4xZhFOSlSEChp+ZxxdAIClO/nlP4DF1KKEUtsJ8vj3js2XmjqMNXQ2QkRxMIHF8vgAv0mqieTNKI09JqhTZJKQvUVVzuQ46AuBKROwJxoylhIRanW7NXWuShvgeFHE8/xlTqJz68Ev7HNV3qR3toKPgNXIiSA+SlVDMIx7/E5g05DtL4IXFBM9fXZt3xTQxE13r+QVJ2FbR1lsX3iiFVnZxCmkCp+h2sBpnsgfFEN+jJpQefxeHu8ty1Y5Ve3FQEgS26hUDCfgk6VAUyqm3UAMrxM5mPSB2+EQf/GlRrnw9g1FJjP/rs=";
        String texture = "ewogICJ0aW1lc3RhbXAiIDogMTY3MTAzMjg3MDYwNiwKICAicHJvZmlsZUlkIiA6ICJmN2I2YTE1Njk3Mjk0YzEzYjZkNzFkNTkwNTAxMDY3MSIsCiAgInByb2ZpbGVOYW1lIiA6ICJzdXBlcm9yZW8iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmQ5ODUyODJhYTAwNGE0MWQ5NTdiNWJiNjhlNWMwNzRjNDgyZTIwNGZkMGMwZDVmZGNkZDZlNGVlZTA4MzUyYyIKICAgIH0sCiAgICAiQ0FQRSIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjM0MGMwZTAzZGQyNGExMWIxNWE4YjMzYzJhN2U5ZTMyYWJiMjA1MWIyNDgxZDBiYTdkZWZkNjM1Y2E3YTkzMyIKICAgIH0KICB9Cn0=";
        gameProfile.getProperties().put("textures", new Property("textures", texture, signature));
        //todo 닉네임으로 스킨 불러와서 넣어주기

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

        PlayerQuitEvent playerQuitEvent = new PlayerQuitEvent(craftServer.getPlayer(entityPlayer), entityPlayer.getName() + " left the game (NPC)");

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

        NPCPlayer.getNPCPlayerList().remove(player);
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
        return entityPlayer.getName() + " joined the game (NPC)";
    }
}
