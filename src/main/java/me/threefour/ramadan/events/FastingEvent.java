package me.threefour.ramadan.events;

import me.threefour.ramadan.Ramadan;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class FastingEvent implements Listener {

    private final List<Player> fastingPlayers = new ArrayList<>();
    private final Ramadan plugin;

    public FastingEvent(Ramadan ramadan) {
        this.plugin = ramadan;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Add player to fasting list
        if (!fastingPlayers.contains(player)) {
            fastingPlayers.add(player);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Check if player is fasting
        if (fastingPlayers.contains(player)) {
            // Add hunger effect
            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20, 0));
        }
    }

    public void startFasting() {
        // Start fasting task
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!fastingPlayers.contains(player)) {
                        fastingPlayers.add(player);
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20 * 60 * 30); // Start every 30 minutes
    }
    public void stopFasting() {
        // Stop fasting task
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.removePotionEffect(PotionEffectType.HUNGER);
        }
        fastingPlayers.clear();
    }

    public void resetFastingTime() {
        long startFastingTime = System.currentTimeMillis() / 1000;
    }

    public void removeFastingPlayer(Player player) {
        if (fastingPlayers.contains(player)) {
            fastingPlayers.remove(player);
            player.removePotionEffect(PotionEffectType.HUNGER);
        }
    }
}
