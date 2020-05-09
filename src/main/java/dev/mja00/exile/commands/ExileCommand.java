package dev.mja00.exile.commands;

import dev.mja00.exile.Exile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import static org.bukkit.Bukkit.getLogger;

public class ExileCommand implements CommandExecutor {

    Plugin plugin = Exile.getPlugin(Exile.class);
    FileConfiguration config = plugin.getConfig();


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player){
            Player senderPlayer = (Player) sender;
            if (args.length == 0){
                senderPlayer.sendMessage("Usage: /exile <user> <report>");
            // The approve section
            }else if(args[0].equalsIgnoreCase("approve")) {
                if (senderPlayer.hasPermission("exile.approve")) {
                    Player target = Bukkit.getPlayerExact(args[1]);
                    if (target == null) {
                        senderPlayer.sendMessage(ChatColor.RED + "You must specify a player to be exiled." + "\nUsage: /exile approve <player>");
                    } else {
                        World world = Bukkit.getWorld(config.getString("world"));
                        int x = config.getInt("x"), y = config.getInt("y"), z = config.getInt("z");
                        Location location = new Location(world, x, y, z);
                        target.teleport(location);
                        senderPlayer.sendMessage(ChatColor.DARK_AQUA + target.getDisplayName() + " has been exiled.");
                        if (config.getBoolean("announceExile")) {
                            sendExiledMessage(target.getDisplayName());
                        }
                    }
                } else {
                    senderPlayer.sendMessage(ChatColor.RED.toString() + "You do not have permission to use this command.");
                }
            // Config settings
            } else if (args[0].equalsIgnoreCase("set")) {
                if (args[1].equalsIgnoreCase("location")) {

                }
            // The exile report section
            } else {
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target == null){
                    senderPlayer.sendMessage(ChatColor.RED + "That player does not exist, try again.");
                } else {
                    String reason = getReport(args, 1);
                    getLogger().info(senderPlayer.getDisplayName() + " wants " + target.getDisplayName() + " exiled because: " + reason);
                    senderPlayer.sendMessage(ChatColor.AQUA + "You write down the reason and attach it to a bird. It flies off in the direction of the castle.");
                    sendExileReport(senderPlayer, target, reason);
                }

            }
        } else {
            System.out.println("Commands must be sent by players.");
        }
        return false;
    }

    public static String getReport(String[] args, int indexStart) {
        StringBuilder output = new StringBuilder();
        for (int i = indexStart; i < args.length; i++) {
            output.append(args[i] + " ");
        }
        return output.toString().trim();
    }

    public static void sendExileReport(Player sender, Player target, String reason) {
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (all.hasPermission("exile.notify")) {
                all.sendMessage(ChatColor.AQUA + "A bird lands at your with a scroll attached to it. It reads: ");
                all.sendMessage(ChatColor.GREEN + "My lord, I'd like " + target.getDisplayName() + " exiled because " + reason + ". Signed " + sender.getDisplayName());
            }
        }
    }

    public static void sendExiledMessage(String player) {
        for (Player all : Bukkit.getOnlinePlayers()) {
            all.sendMessage(ChatColor.GOLD + player + " has been exiled! ");
        }
    }

}
