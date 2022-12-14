package xyz.oreodev.npc.command.shop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class shopCompleter implements TabCompleter {
    List<String> COMMANDS = new ArrayList<>();

    public shopCompleter() {
        COMMANDS.add("list");
        COMMANDS.add("open");
        COMMANDS.add("remove");
        COMMANDS.add("edit");
        COMMANDS.add("create");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        final List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[0], COMMANDS, completions);
        Collections.sort(completions);
        return completions;
    }
}
