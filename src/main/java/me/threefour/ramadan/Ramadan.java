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
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
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

    private boolean enabled = false;
    private BukkitTask resetFastingTask;
    private FastingEvent fastingEvent;

    @Override
    public void onEnable() {
        if (enabled) {
            getLogger().warning("Plugin is already enabled!");
            return;
        }
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
            Objects.requireNonNull(getCommand("quest")).setExecutor(new QuestCommand(this));
        } catch (NullPointerException e) {
            getLogger().warning("Failed to register quest command: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
        PluginCommand iftarCommand = getCommand("iftar");
        if (iftarCommand != null) {
            iftarCommand.setExecutor(new IftarTableCommand(this));
            iftarCommand.setTabCompleter(new IftarTableCommand(this));
        } else {
            getLogger().severe("Could not find iftar command!");
        }
        fastingEvent = new FastingEvent(this);
        getCommand("stopfasting").setExecutor(new StopFastingCommand(fastingEvent));
        PluginCommand reloadCommand = getCommand("ramadan");
        if (reloadCommand != null) {
            reloadCommand.setExecutor(new ReloadCommand(this));
        } else {
            getLogger().severe("Could not find reload command!");
        }

        // Register events
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new IftarLocationListener(this), this);
        pm.registerEvents(new QuestListener(this), this);
        pm.registerEvents(fastingEvent, this);
        if (pm.getPlugin("PlaceholderAPI") != null) {
            new Placeholders(this).register();
        }

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

    public void setIftarLocation(String name, Location location) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "iftardata.yml"));
        if (config != null) {
            ConfigurationSection iftarLocations = config.getConfigurationSection("iftar-locations");
            if (iftarLocations != null) {
                iftarLocations.set(name, location.serialize());
                try {
                    config.save(new File(getDataFolder(), "iftardata.yml"));
                } catch (IOException e) {
                    getLogger().warning("Failed to save iftar location: " + e.getMessage());
                }
            }
        }
    }

    public void teleportToIftarLocation(Player player, String locationName) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "iftardata.yml"));
        if (config.contains("iftar-locations." + locationName)) {
            ConfigurationSection locationConfig = config.getConfigurationSection("iftar-locations." + locationName);
            String worldName = locationConfig.getString("world");
            if (worldName != null && Bukkit.getWorld(worldName) != null) {
                double x = locationConfig.getDouble("x");
                double y = locationConfig.getDouble("y");
                double z = locationConfig.getDouble("z");
                float yaw = (float) locationConfig.getDouble("yaw");
                float pitch = (float) locationConfig.getDouble("pitch");
                World world = Bukkit.getWorld(worldName);
                Location location = new Location(world, x, y, z, yaw, pitch);
                player.teleport(location);
            } else {
                player.sendMessage("The world specified for this location does not exist.");
            }
        } else {
            player.sendMessage("This location is not set.");
        }
    }

    public Location getIftarLocation(String name) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "iftardata.yml"));
        ConfigurationSection locationConfig = config.getConfigurationSection("iftar-locations." + name);
        if (locationConfig == null) {
            // Handle error: Configuration section not found
            return null;
        }

        String worldName = locationConfig.getString("world");
        if (worldName == null || Bukkit.getWorld(worldName) == null) {
            // Handle error: World not found
            return null;
        }

        double x = locationConfig.getDouble("x");
        double y = locationConfig.getDouble("y");
        double z = locationConfig.getDouble("z");
        float yaw = (float) locationConfig.getDouble("yaw");
        float pitch = (float) locationConfig.getDouble("pitch");
        World world = Bukkit.getWorld(worldName);
        Location location = new Location(world, x, y, z, yaw, pitch);
        return location;
    }
}