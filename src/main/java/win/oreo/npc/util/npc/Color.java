package win.oreo.npc.util.npc;

import org.bukkit.ChatColor;

public class Color {
    public static String format(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
