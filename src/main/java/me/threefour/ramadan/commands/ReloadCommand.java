package me.threefour.ramadan.commands;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ReloadCommand implements CommandExecutor {

    private final Plugin plugin;

    public ReloadCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1 || !args[0].equalsIgnoreCase("reload")) {
            return false;
        }

        if (!sender.hasPermission("ramadan.reload")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        plugin.reloadConfig();
        reloadIftarData();
        reloadQuests();

        sender.sendMessage(ChatColor.GREEN + "Configuration files reloaded successfully.");
        return true;
    }

    private void reloadIftarData() {
        File iftarDataFile = new File(plugin.getDataFolder(), "iftardata.yml");
        if (!iftarDataFile.exists()) {
            plugin.saveResource("iftardata.yml", false);
        }
        FileConfiguration iftarDataConfig = YamlConfiguration.loadConfiguration(iftarDataFile);
        iftarDataConfig.options().copyDefaults(true);
        try {
            iftarDataConfig.save(iftarDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reloadQuests() {
        File questsFile = new File(plugin.getDataFolder(), "quests.yml");
        if (!questsFile.exists()) {
            plugin.saveResource("quests.yml", false);
        }
        FileConfiguration questsConfig = YamlConfiguration.loadConfiguration(questsFile);
        questsConfig.options().copyDefaults(true);
        try {
            questsConfig.save(questsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
