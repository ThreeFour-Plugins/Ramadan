package me.threefour.ramadan.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.threefour.ramadan.Ramadan;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class Placeholders extends PlaceholderExpansion {
    private final Plugin plugin;

    public Placeholders(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "Ramadan";
    }

    @Override
    public @NotNull String getAuthor() {
        return "ThreeFour";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.1";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.equalsIgnoreCase("iftar_location")) {
            Ramadan plugin = (Ramadan) this.plugin;
            Location iftarLocation = plugin.getIftarLocation();
            if (iftarLocation != null) {
                return iftarLocation.getBlockX() + ", " + iftarLocation.getBlockY() + ", " + iftarLocation.getBlockZ();
            } else {
                return "not set";
            }
        } else if (params.equalsIgnoreCase("completed_quests")) {
            File file = new File(plugin.getDataFolder(), "quests.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            if (player == null) {
                return "0";
            }

            List<String> completedQuests = config.getStringList("players." + player.getUniqueId() + ".completedQuests");
            return String.valueOf(completedQuests.size());
        }
        return null;
    }
}
