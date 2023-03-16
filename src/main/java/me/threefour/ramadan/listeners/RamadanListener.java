package me.threefour.ramadan.listeners;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.plugin.Plugin;

public class RamadanListener implements Listener {

    private Plugin plugin;
    private final World world;
    private final long startTime;
    private final long endTime;

    public RamadanListener() {
        this.world = Bukkit.getWorlds().get(0); // use the first world, change this if necessary
        this.startTime = 12300L; // start time at 12,300 ticks
        this.endTime = 23850L; // end time at 23,850 ticks
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        sendRamadanTitle(player);
    }

    @EventHandler
    public void onTimeChange(TimeSkipEvent event) {
        long time = event.getSkipAmount() + world.getTime();

        // Check if it's between sunset and midnight
        if (time >= startTime && time < 18000) {
            sendRamadanTitleToAllPlayers("Ramadan Kareem", "It's time to break your fast!");
        }
        // Check if it's after midnight
        else if (time >= 18000 && time < endTime) {
            sendRamadanTitleToAllPlayers("Ramadan Mubarak", "Welcome to a new day of fasting!");
        }
        // Check if it's after Ramadan
        else if (time >= endTime) {
            sendRamadanTitleToAllPlayers("Ramadan has ended", "Thank you for fasting with us!");
        }
    }

    private void sendRamadanTitle(Player player) {
        long time = world.getTime();
        // Check if it's between sunset and midnight
        if (time >= startTime && time < 18000) {
            player.sendTitle("Ramadan Kareem", "It's time to break your fast!", 10, 70, 20);
        }
        // Check if it's after midnight
        else if (time >= 18000 && time < endTime) {
            player.sendTitle("Ramadan Mubarak", "Welcome to a new day of fasting!", 10, 70, 20);
        }
        // Check if it's after Ramadan
        else if (time >= endTime) {
            player.sendTitle("Ramadan has ended", "Thank you for fasting with us!", 10, 70, 20);
        }
    }

    private void sendRamadanTitleToAllPlayers(String title, String subtitle) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(title, subtitle, 10, 70, 20);
        }
    }
}

