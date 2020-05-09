package dev.mja00.exile.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExileTabCompletetion implements TabCompleter {


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> playerNames = onlinePlayers();
            playerNames.add("approve");
            playerNames.add("set");
            return playerNames;
        } else if (args.length == 2){
            if (args[0].equalsIgnoreCase("set")) {
                List<String> options = Arrays.asList("location", "announce");
                return options;
            } else {
                return onlinePlayers();
            }
        }


        return null;
    }

    public static List<String> onlinePlayers() {
        List<String> playerNames = new ArrayList<>();
        for (Player all : Bukkit.getOnlinePlayers()) {
            playerNames.add(all.getDisplayName());
        }
        return playerNames;
    }
}
