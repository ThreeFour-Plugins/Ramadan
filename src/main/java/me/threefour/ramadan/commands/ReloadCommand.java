package me.threefour.ramadan.commands;

import me.threefour.ramadan.Ramadan;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ReloadCommand implements CommandExecutor {


    private final Ramadan plugin;

    public ReloadCommand(Ramadan ramadan) {
        this.plugin = ramadan;
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
