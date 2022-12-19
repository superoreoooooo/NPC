package win.oreo.npc.command.item;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import win.oreo.npc.Main;
import win.oreo.npc.util.item.itemUtil;
import win.oreo.npc.util.npc.NPCPlayer;

import java.util.ArrayList;
import java.util.List;

public class itemCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("remove");
            completions.add("list");
            completions.add("set");
            completions.add("give");
            return completions;
        }
        else if (args.length == 2) {
            switch (args[0]) {
                case "give":
                    List<String> names = new ArrayList<>();
                    itemUtil.priceMap.keySet().forEach(itemStack -> names.add(itemUtil.getItemName(itemStack)));
                    completions.addAll(names);
            }
            return completions;
        }
        return null;
    }
}
