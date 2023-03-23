package me.threefour.ramadan.commands;

import me.threefour.ramadan.Ramadan;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IftarTableCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (!player.hasPermission("ramadan.iftar")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }


        if (args.length == 0) {
            player.sendMessage("Usage: /iftar <set|tp|get>");
            return true;
        }

        Ramadan plugin = new Ramadan();
        if (args[0].equalsIgnoreCase("set")) {
            if (args.length != 4) {
                player.sendMessage("Usage: /iftar set <x> <y> <z>");
                return true;
            }
            try {
                double x = Double.parseDouble(args[1]);
                double y = Double.parseDouble(args[2]);
                double z = Double.parseDouble(args[3]);
                Location location = new Location(player.getWorld(), x, y, z);
                plugin.setIftarLocation(location);
                player.sendMessage("Iftar location set to (" + x + ", " + y + ", " + z + ")");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid coordinates. Usage: /iftar set <x> <y> <z>");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            }
            return true;
        } else if (args[0].equalsIgnoreCase("tp")) {
            plugin.teleportToIftarLocation(player);
            return true;
        } else if (args[0].equalsIgnoreCase("get")) {
            ItemStack heldItem = player.getInventory().getItemInMainHand();
            if (heldItem.getType() != Material.COMPASS) {
                player.sendMessage("You need a compass to get the Iftar table location.");
                return true;
            }

            Location iftarLocation = plugin.getIftarLocation();
            if (iftarLocation == null) {
                player.sendMessage("Iftar table location has not been set.");
                return true;
            }

            ItemMeta compassMeta = heldItem.getItemMeta();
            compassMeta.setDisplayName("IftarTableCompass");
            compassMeta.setLore(Collections.singletonList("Points to the Iftar table location."));
            heldItem.setItemMeta(compassMeta);

            CompassMeta compassMeta1 = (CompassMeta) heldItem.getItemMeta();
            compassMeta1.setLodestoneTracked(false);
            compassMeta1.setLodestone(iftarLocation);
            heldItem.setItemMeta(compassMeta1);

            player.sendMessage("The compass now points to the Iftar table location.");
            return true;
        }

        return false;
    }


    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("set", "tp", "get");
        }

        return Collections.emptyList();
    }
}
