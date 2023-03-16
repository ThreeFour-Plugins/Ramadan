package me.threefour.ramadan.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.plugin.Plugin;

public class RamadanEvent implements Listener {

    private final Plugin plugin;
    private final String cancelledMessage;

    public RamadanEvent(Plugin plugin) {
        this.plugin = plugin;
        this.cancelledMessage = ChatColor.RED + "You cannot eat or drink during daylight hours in Ramadan!";
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (cancelledMessage == null) {
            event.setCancelled(true);
            return;
        }
        Player player = event.getPlayer();
        long time = player.getWorld().getTime();

        if (time < 12300L) {
            event.setCancelled(true);
            player.sendMessage(cancelledMessage);
        }
    }
}
