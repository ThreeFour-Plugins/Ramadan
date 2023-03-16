package me.threefour.ramadan.update;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GithubUpdate {

    private final Plugin plugin;
    private final String repository;
    private final String currentVersion;
    private String latestVersion;

    public GithubUpdate(Plugin plugin, String repository, String currentVersion) {
        this.plugin = plugin;
        this.repository = repository;
        this.currentVersion = currentVersion;
    }

    public void checkForUpdates() {
        FileConfiguration config = plugin.getConfig();
        boolean updateEnabled = config.getBoolean("update", true);
        if (!updateEnabled) {
            plugin.getLogger().info("Update check is disabled in the config file.");
            return;
        }

        try {
            URL url = new URL("https://api.github.com/repos/" + repository + "/releases/latest");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

            if (connection.getResponseCode() != 200) {
                throw new IOException("Failed to check for updates. HTTP error code: " + connection.getResponseCode());
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            reader.close();
            connection.disconnect();

            JsonObject response = new Gson().fromJson(responseBuilder.toString(), JsonObject.class);
            String latestTag = response.get("tag_name").getAsString();
            latestVersion = latestTag.startsWith("v") ? latestTag.substring(1) : latestTag;

            if (isNewerVersionAvailable()) {
                plugin.getLogger().warning("A new version (" + latestVersion + ") of the plugin is available on Github: https://github.com/" + repository);
            } else {
                plugin.getLogger().info("Plugin is up to date!");
            }

        } catch (IOException e) {
            plugin.getLogger().warning("Failed to check for updates: " + e.getMessage());
        }
    }

    public boolean isNewerVersionAvailable() {
        return latestVersion != null && !latestVersion.equals(currentVersion);
    }

    public String getLatestVersion() {
        return latestVersion;
    }
}
