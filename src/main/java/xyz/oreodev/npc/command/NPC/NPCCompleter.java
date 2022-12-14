package xyz.oreodev.npc.command.NPC;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NPCCompleter implements TabCompleter {
    List<String> commands = new ArrayList<>();

    public NPCCompleter() {
        commands.add("list");
        commands.add("add");
        commands.add("remove");
        commands.add("reload");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        final List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[0], commands, completions);
        Collections.sort(completions);
        return completions;
    }
}
