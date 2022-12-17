package xyz.oreodev.npc.command.account;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import xyz.oreodev.npc.util.npc.NPCPlayer;

import java.util.ArrayList;
import java.util.List;

public class accountCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("help");
            completions.add("now");
            completions.add("list");
            completions.add("set");
            completions.add("send");
            completions.add("add");
            completions.add("remove");
            return completions;
        }
        else if (args.length == 2) {
            switch (args[0]) {
                case "send" :
                case "remove" :
                case "set" :
                case "add" :
                    for (NPCPlayer player : NPCPlayer.getNPCPlayerList()) {
                        completions.add(player.getName());
                    }
                    break;
            }
            return completions;
        }
        return null;
    }
}
