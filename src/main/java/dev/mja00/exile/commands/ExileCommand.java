package dev.mja00.exile.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getLogger;

public class ExileCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player){
            Player senderPlayer = (Player) sender;
            if (args.length == 0){
                senderPlayer.sendMessage("Usage: /exile <user> <report>");
            }else if(args[0].equalsIgnoreCase("approve")) {
                if (senderPlayer.hasPermission("exile.approve")) {
                    senderPlayer.sendMessage("You've been exiled.");
                } else {
                    senderPlayer.sendMessage(ChatColor.RED.toString() + "You do not have permission to use this command.");
                }
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

}
