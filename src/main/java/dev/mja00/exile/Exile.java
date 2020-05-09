package dev.mja00.exile;

import dev.mja00.exile.commands.ExileCommand;
import dev.mja00.exile.commands.ExileTabCompletetion;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public final class Exile extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Loading commands.");
        getCommand("exile").setExecutor(new ExileCommand());
        getCommand("exile").setTabCompleter(new ExileTabCompletetion());
        getLogger().info("Loading the config.");
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        getLogger().info("Loaded and enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Unloaded and disabled.");

    }


}
