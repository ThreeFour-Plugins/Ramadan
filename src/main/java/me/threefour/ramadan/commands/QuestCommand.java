package me.threefour.ramadan.commands;

import me.threefour.ramadan.Ramadan;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static com.google.common.io.Resources.getResource;
import static org.bukkit.Bukkit.getLogger;

public class QuestCommand implements CommandExecutor {

    private List<String> questList;

    public QuestCommand() throws IOException {
        loadQuests();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (!sender.hasPermission("ramadan.quest")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        if (args.length >= 3 && args[0].equalsIgnoreCase("add")) {
            String newQuest = args[1];
            String reward = args[2];
            try {
                addQuest(newQuest, reward, sender);
                sender.sendMessage(ChatColor.GREEN + "Quest added: " + newQuest);
            } catch (IOException e) {
                sender.sendMessage(ChatColor.RED + "Failed to add quest: " + e.getMessage());
            }
            return true;
        }

        if (args.length == 0) {
            // Display quest list
            sender.sendMessage(ChatColor.GREEN + "Available quests:");

            for (String quest : questList) {
                sender.sendMessage(ChatColor.YELLOW + "- " + quest);
            }
        } else {
            // Parse selected quest
            String selectedQuest = args[0];

            if (questList.contains(selectedQuest)) {
                // Implement reward logic here
                sender.sendMessage(ChatColor.GREEN + "You have completed the " + selectedQuest + " quest!");
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid quest selected. Use /quest to see available quests.");
            }
        }

        return true;
    }

    private void loadQuests() throws IOException {
        // Get the file path and create a new File object
        File file = new File(getDataFolder(), "quests.yml");

        if (!file.exists()) {
            // If the file does not exist, try to create it
            saveResource("quests.yml");
        }

        // Load the configuration from the file
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        // Get the quest list from the configuration
        questList = config.getStringList("quests");
    }
    private File getDataFolder() {
        Plugin plugin = Ramadan.getPlugin(Ramadan.class);
        return plugin.getDataFolder();
    }

    private void saveResource(String s) throws IOException {
        // Get the file object for the resource
        File resourceFile = new File(getDataFolder(), s);

        // Only save the resource if it doesn't already exist
        if (!resourceFile.exists()) {
            // Get the input stream for the resource
            InputStream resourceStream = getResource(s).openStream();

            // If the resource stream is null, the resource doesn't exist in the JAR file
            if (resourceStream == null) {
                getLogger().warning("Resource not found: " + s);
                return;
            }

            // Copy the resource from the JAR file to the data folder
            try (InputStream in = resourceStream) {
                Files.copy(in, resourceFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                getLogger().severe("Failed to save resource " + s + ": " + e.getMessage());
            }
        }
    }
    private void addQuest(String questName, String reward, CommandSender sender) throws IOException {
        File file = new File(getDataFolder(), "quests.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        // Get the existing quest list
        List<String> quests = config.getStringList("quests");

        // Add the new quest with its reward
        String newQuest = questName + ":" + reward;
        quests.add(newQuest);

        // Save the updated quest list
        config.set("quests", quests);
        config.save(file);

        // Add the completed quest to the player's list
        if (sender instanceof Player player) {
            List<String> completedQuests = config.getStringList("players." + player.getUniqueId() + ".completedQuests");
            completedQuests.add(newQuest);
            config.set("players." + player.getUniqueId() + ".completedQuests", completedQuests);
            config.save(file);
        }
    }
}