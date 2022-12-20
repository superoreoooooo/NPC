package win.oreo.npc.version;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import win.oreo.npc.util.npc.NPCPlayer;
import win.oreo.npc.Main;
import win.oreo.npc.paper.PaperUtils_v1_12_R1;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.UUID;

public class v1_12_R1 {
    public static EntityPlayer spawn(NPCPlayer NPCPlayer, double x, double y, double z) {
        WorldServer worldServer = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
        MinecraftServer mcServer = ((CraftServer) Bukkit.getServer()).getServer();
        EntityPlayer entityPlayer = createEntityPlayer(NPCPlayer.getUUID(), NPCPlayer.getName(), worldServer, NPCPlayer.getSkinName());
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

        PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(((CraftServer)Bukkit.getServer()).getPlayer(entityPlayer), joinMessage);
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

        entityPlayer.getBukkitEntity().spigot().setCollidesWithEntities(false);

        return entityPlayer;
    }

    public static String[] getSkinData(String skinOwner) { //TODO 스킨 데이터 저장했다가 불러오는식으로 변경하기 (현재 개느려터짐)
        String[] str = new String[2];

        if (getUUID(skinOwner) == null || skinOwner.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")) {
            str[0] = "ewogICJ0aW1lc3RhbXAiIDogMTY3MTUyOTcyOTIyMSwKICAicHJvZmlsZUlkIiA6ICI4MzYyYTJlM2EyNmI0NGUzODdjNTQ4YjlmMjlhZjk4MCIsCiAgInByb2ZpbGVOYW1lIiA6ICJPcHhuIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzE2ZWJiOWQ2ZjBlMGQwNDYxNmUxZjY4MjAxY2M1YWQ5Y2I3ODdhMmU4Mjc2MjJiMTcwODA3NmMxYjcxMGIyZjkiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfSwKICAgICJDQVBFIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yMzQwYzBlMDNkZDI0YTExYjE1YThiMzNjMmE3ZTllMzJhYmIyMDUxYjI0ODFkMGJhN2RlZmQ2MzVjYTdhOTMzIgogICAgfQogIH0KfQ==";
            str[1] = "PDe/7bkj45+YwlgMSAmpH0G/qATV8RmxemrGUjb90fhGpzrU6TYoEupVlzud1H8Gh2gDu42po5iwN5vdrxv7JYxjdesDe3Avg/1i48+Z8ka+5gcaNKuTiwKj78P6yziXW3PK6SrtZEyTVOssrajcXJ4navW50UiLYmwV9o18GSzI56C5dn7b7mWMMPEfaQ7tiv2Ut1ZAlGt9pgdf3EGf2UHFngFYzSvSvE/V1l6i4Uhd68cBrTLawxOds+q9Ul672YareeUfIhbxKa1dAH/y2M8xEnznEX8I+QYxqdLnP7eXCPoyuX9+sZhh3mcJ7Ot35BOVZrkotrpQhjSK2Kx7P5VogY6r1nrIO24mE4wTvrIr4ME5YKiP5cRIHZYimbUJU7+Jsgx9S6kdQ9CRUs+PEhJRKndWO/zqUgh2U0/g5Bpg4vFGQQ1LE0BCUIjZrRljAB8k2dvBm9IXUgRu95p2/9iipCXDVQL6E4mMNpZ+rZi+UHo3fVI6brS/1Crsr8hGZRGTSP2AHwJR0fvjYx1OIh9O5Uz1KaCvyz3c1FfWQh50Ayj/2ArR8beP7pZNTlg6SR0z/9/ffN4q7rdqClcrJQG8vfo/kP78JaVdO69c3CnSeUzz0HmY2YXYgtt1zGdy0dF/Mrj0oOfTe7aYp8TdtL/VsAdjiDBjxnUU4nqCTcw=";
        }

        else try {
            URL profile = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + getUUID(skinOwner) + "?unsigned=false");
            InputStreamReader reader = new InputStreamReader(profile.openStream());
            JsonObject textureProperty = new JsonParser().parse(reader).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            str[0] = textureProperty.get("value").getAsString();
            str[1] = textureProperty.get("signature").getAsString();
        } catch (IOException ignored) {}
        return str;
    }

    private static String getUUID(String name) {
        try {
            URL profile = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            InputStreamReader reader = new InputStreamReader(profile.openStream());
            JsonElement element = new JsonParser().parse(reader);
            if (!(element instanceof JsonObject)) {
                return null;
            }

            JsonObject object = element.getAsJsonObject();
            return object.get("id").getAsString();
        } catch (IOException ignored) {}
        return null;
    }

    public static EntityPlayer createEntityPlayer(UUID uuid, String name, WorldServer worldServer, String skinName) {
        MinecraftServer mcServer = ((CraftServer) Bukkit.getServer()).getServer();
        GameProfile gameProfile = new GameProfile(uuid, name);
        String value = getSkinData(skinName)[0];
        String signature = getSkinData(skinName)[1];
        gameProfile.getProperties().put("textures", new Property("textures", value, signature));

        return new EntityPlayer(mcServer, worldServer, gameProfile, new PlayerInteractManager(worldServer));
    }

    public static void removePlayer(NPCPlayer player) {
        MinecraftServer mcServer = ((CraftServer) Bukkit.getServer()).getServer();
        CraftServer craftServer = (CraftServer) Bukkit.getServer();
        EntityPlayer entityPlayer = player.getEntityPlayer();
        WorldServer worldServer = entityPlayer.getWorld().getWorld().getHandle();

        if (entityPlayer.activeContainer != entityPlayer.defaultContainer) {
            entityPlayer.closeInventory();
        }

        PlayerQuitEvent playerQuitEvent = new PlayerQuitEvent(craftServer.getPlayer(entityPlayer), "");

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
        return "";
    }
}
