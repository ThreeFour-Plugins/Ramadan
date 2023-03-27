package me.threefour.ramadan.listeners;

import me.threefour.ramadan.Ramadan;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class IftarLocationListener implements Listener {
    private final Ramadan plugin;

    public IftarLocationListener(Ramadan plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Location fromLocation = event.getFrom();
        Location toLocation = event.getTo();

        if (fromLocation.getWorld() != toLocation.getWorld()) {
            return;
        }

        String name = "";
        Location iftarLocation = plugin.getIftarLocation(name);
        if (iftarLocation != null && toLocation.distanceSquared(iftarLocation) <= 1) {
            player.sendMessage(ChatColor.GOLD + "Welcome to the Iftar location!");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        String name = "";
        Location iftarLocation = plugin.getIftarLocation(name);
        if (iftarLocation != null && event.getClickedBlock() != null && event.getClickedBlock().getLocation().equals(iftarLocation)) {
            player.sendMessage(ChatColor.GOLD + "This is the Iftar location!");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        }
    }
}
