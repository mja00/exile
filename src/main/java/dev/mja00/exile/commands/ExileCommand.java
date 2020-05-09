package dev.mja00.exile.commands;

import dev.mja00.exile.Exile;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import net.md_5.bungee.api.chat.TextComponent;
import org.w3c.dom.Text;

import java.awt.*;

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
                if (senderPlayer.hasPermission("exile.edit")) {
                    if (args[1].equalsIgnoreCase("location")) {
                        Location playerLoc = senderPlayer.getLocation();
                        String worldName = playerLoc.getWorld().getName();
                        int x = playerLoc.getBlockX(), y = playerLoc.getBlockY(), z = playerLoc.getBlockZ();
                        config.set("world", worldName);
                        config.set("x", x);
                        config.set("y", y);
                        config.set("z", z);
                        plugin.saveConfig();
                        senderPlayer.sendMessage(ChatColor.GREEN + "Config updated.");
                    } else if (args[1].equalsIgnoreCase("announce")) {
                        if (args.length <= 2) {
                            config.set("announceExile", !config.getBoolean("announceExile"));
                            plugin.saveConfig();
                        } else if (args[2].equalsIgnoreCase("true")) {
                            config.set("announceExile", true);
                            plugin.saveConfig();
                        } else {
                            config.set("announceExile", false);
                            plugin.saveConfig();
                        }
                        senderPlayer.sendMessage(ChatColor.GREEN + "Config updated. Announce Exile set to " + config.getBoolean("announceExile"));

                    }
                }
            // The exile report section
            } else {
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target == null){
                    senderPlayer.sendMessage(ChatColor.RED + "That player does not exist, try again.");
                } else {
                    if (!target.hasPermission("exile.immune")) {
                        senderPlayer.sendMessage(ChatColor.RED + "This player is immune to being exiled.");
                    } else {
                        String reason = getReport(args, 1);
                        getLogger().info(senderPlayer.getDisplayName() + " wants " + target.getDisplayName() + " exiled because: " + reason);
                        senderPlayer.sendMessage(ChatColor.AQUA + "You write down the reason and attach it to a bird. It flies off in the direction of the castle.");
                        sendExileReport(senderPlayer, target, reason);
                        senderPlayer.playSound(senderPlayer.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1f, 1f);
                    }
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
                all.playSound(all.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1f, 1f);
                all.sendMessage(ChatColor.AQUA + "A bird lands at your feet with a scroll attached to it. It reads: ");
                all.sendMessage(ChatColor.GREEN + "My lord, \nI'd like " + target.getDisplayName() + " exiled because " + reason + ". \n- Signed " + sender.getDisplayName());
                TextComponent confirm = new TextComponent("[Exile]");
                confirm.setColor(net.md_5.bungee.api.ChatColor.RED);
                confirm.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/exile approve " + target.getDisplayName()));
                confirm.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("Click this to exile " + target.getDisplayName()).color(net.md_5.bungee.api.ChatColor.GRAY).create()));
                TextComponent pardon = new TextComponent("[Pardon]");
                pardon.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                pardon.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/exile pardon"));
                pardon.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("Click this to pardon " + target.getDisplayName()).color(net.md_5.bungee.api.ChatColor.GRAY).create()));
                confirm.addExtra(new TextComponent(" "));
                confirm.addExtra(pardon);
                all.spigot().sendMessage(confirm);
            }
        }
    }

    public static void sendExiledMessage(String player) {
        for (Player all : Bukkit.getOnlinePlayers()) {
            all.sendMessage(ChatColor.GOLD + player + " has been exiled! ");
        }
    }

}
