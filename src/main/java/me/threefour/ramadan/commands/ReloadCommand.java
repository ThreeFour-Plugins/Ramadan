package me.threefour.ramadan.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class ReloadCommand implements CommandExecutor {
    private Plugin plugin;

    public ReloadCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Display help message or default command behavior
            sender.sendMessage("Unknown command. Usage: /ramadan help");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("ramadan.reload")) {
                sender.sendMessage("You do not have permission to use this command.");
                return true;
            }
            plugin.reloadConfig(); // Reload the config.yml file
            sender.sendMessage("Config reloaded.");
            return true;
        }

        // Handle other subcommands or display help message
        sender.sendMessage("Unknown command. Usage: /ramadan help");
        return true;
    }

}