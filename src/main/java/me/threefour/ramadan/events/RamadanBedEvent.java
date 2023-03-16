package me.threefour.ramadan.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.plugin.Plugin;

public class RamadanBedEvent implements Listener {

    private Plugin plugin;

    public void setRamadanEnabled(boolean ramadanEnabled) {
        if (ramadanEnabled) {
            plugin.getServer().broadcastMessage(ChatColor.GOLD + "Ramadan has started!");
        } else {
            plugin.getServer().broadcastMessage(ChatColor.GOLD + "Ramadan has ended!");
        }
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        BlockData bedBlockData = event.getBed().getBlockData();
        Material bedMaterial = bedBlockData.getMaterial();
        long time = world.getTime();

        if (time >= 12960L && time <= 23100L && bedMaterial == Material.RED_BED) {
            event.setCancelled(true);
            String bedCancelledMessage = ChatColor.translateAlternateColorCodes('&', "You cannot sleep during Ramadan!");
            player.sendMessage(bedCancelledMessage);
        }
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }
}
