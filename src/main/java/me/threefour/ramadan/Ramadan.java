package me.threefour.ramadan;

import me.threefour.ramadan.commands.IftarTableCommand;
import me.threefour.ramadan.commands.QuestCommand;
import me.threefour.ramadan.commands.ReloadCommand;
import me.threefour.ramadan.commands.StopFastingCommand;
import me.threefour.ramadan.events.FastingEvent;
import me.threefour.ramadan.listeners.IftarLocationListener;
import me.threefour.ramadan.listeners.QuestListener;
import me.threefour.ramadan.placeholders.Placeholders;
import me.threefour.ramadan.tasks.ResetFastingTask;
import me.threefour.ramadan.update.GithubUpdate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class Ramadan extends JavaPlugin {

    private boolean enabled;
    private BukkitTask resetFastingTask;
    @Override
    public void onEnable() {
        if (enabled) {
            getLogger().warning("Plugin is already enabled!");
            return;
        }
        // Set enabled to true to indicate that the plugin is running
        enabled = true;

        // Initialize update checker
        GithubUpdate updateChecker = new GithubUpdate(this, "ThreeFour-Plugins/Ramadan", getDescription().getVersion());
        updateChecker.checkForUpdates();

        // Load configuration files
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }

        File questsFile = new File(getDataFolder(), "quests.yml");
        if (!questsFile.exists()) {
            saveResource("quests.yml", false);
        }

        File iftarDataFile = new File(getDataFolder(), "iftardata.yml");
        if (!iftarDataFile.exists()) {
            saveResource("iftardata.yml", false);
        }

        // Register commands
        try {
            Objects.requireNonNull(getCommand("quest")).setExecutor(new QuestCommand());
        } catch (NullPointerException e) {
            getLogger().warning("Failed to register quest command: " + e.getMessage());
        } catch (IOException e) {
            getLogger().warning("Failed to create QuestCommand instance: " + e.getMessage());
        }
        getCommand("iftar").setExecutor(new IftarTableCommand());
        getCommand("iftar").setTabCompleter(new IftarTableCommand());
        FastingEvent fastingEvent = new FastingEvent(this);
        getCommand("stopfasting").setExecutor(new StopFastingCommand(fastingEvent));
        Objects.requireNonNull(getCommand("ramadan")).setExecutor(new ReloadCommand(this));

        // Register events
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new IftarLocationListener(this), this);
        pm.registerEvents(new QuestListener(this), this);
        pm.registerEvents(new FastingEvent(this), this);
        if (pm.getPlugin("PlaceholderAPI") != null) {
            new Placeholders(this).register();
        }

        // Initialize FastingEvent
        fastingEvent = new FastingEvent(this);

        // Start fasting
        fastingEvent.startFasting();

        // Schedule ResetFastingTask to run every fasting duration
        resetFastingTask = Bukkit.getScheduler().runTaskTimer(this, new ResetFastingTask(fastingEvent), 0, 20L * getConfig().getInt("fasting-duration"));

        getLogger().info("Ramadan Plugin enabled.");
    }
    public void onDisable() {
        // Set enabled to false to indicate that the plugin is no longer running
        enabled = false;
        saveConfig();
        getLogger().info("Ramadan Plugin disabled.");
    }
    public List<String> getCompletedQuests(Player player) {
        List<String> completedQuests = new ArrayList<>();

        // Retrieve completed quests for the player from the quests.yml file
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "quests.yml"));
        if (config.contains("players." + player.getUniqueId())) {
            List<String> questIds = config.getStringList("players." + player.getUniqueId());
            // Retrieve quest names from the quest IDs
            for (String questId : questIds) {
                String questName = config.getString("quests." + questId + ".name");
                if (questName != null) {
                    completedQuests.add("- " + questName);
                }
            }
        }
        return completedQuests;
    }
    public ResourceBundle getQuestRewards() {
        FileConfiguration config = this.getConfig();
        if (!config.contains("quests")) {
            this.saveResource("quests.yml", false);
        }
        FileConfiguration questConfig = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "quests.yml"));
        return new ResourceBundle() {
            @Override
            protected Object handleGetObject(String key) {
                return questConfig.get(key);
            }

            @Override
            public Enumeration<String> getKeys() {
                return Collections.enumeration(questConfig.getKeys(false));
            }
        };
    }

    public void setIftarLocation(Location location) {
        File file = new File(this.getDataFolder(), "iftardata.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String worldName = config.getString("iftar_location.world");
        config.set("iftar_location.world", location.getWorld().getName());
        config.set("iftar_location.x", location.getX());
        config.set("iftar_location.y", location.getY());
        config.set("iftar_location.z", location.getZ());
        config.set("iftar-location.yaw", location.getYaw());
        config.set("iftar-location.pitch", location.getPitch());

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void teleportToIftarLocation(Player player) {
        FileConfiguration config = getConfig();
        if (config.contains("iftar-location")) {
            String worldName = config.getString("iftar-location.world");
            if (worldName != null) { // Add null check
                double x = config.getDouble("iftar-location.x");
                double y = config.getDouble("iftar-location.y");
                double z = config.getDouble("iftar-location.z");
                float yaw = (float) config.getDouble("iftar-location.yaw");
                float pitch = (float) config.getDouble("iftar-location.pitch");
                World world = getServer().getWorld(worldName);
                if (world != null) {
                    Location location = new Location(world, x, y, z, yaw, pitch);
                    player.teleport(location);
                } else {
                    player.sendMessage("The world " + worldName + " does not exist.");
                }
            } else {
                player.sendMessage("The iftar location world name is null.");
            }
        } else {
            player.sendMessage("No iftar location set.");
        }
    }
    public Location getIftarLocation() {
        FileConfiguration config = this.getConfig();
        if (config.contains("iftar_location")) {
            String worldName = config.getString("iftar_location.world");
            double x = config.getDouble("iftar-location.x");
            double y = config.getDouble("iftar-location.y");
            double z = config.getDouble("iftar-location.z");
            float yaw = (float) config.getDouble("iftar_location.yaw");
            float pitch = (float) config.getDouble("iftar_location.pitch");
            assert worldName != null;
            return new Location(this.getServer().getWorld(worldName), x, y, z, yaw, pitch);
        } else {
            return null;
        }
    }

}