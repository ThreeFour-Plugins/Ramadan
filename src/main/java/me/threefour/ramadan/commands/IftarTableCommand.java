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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class IftarTableCommand implements CommandExecutor, TabCompleter {
    private final Ramadan ramadan;
    public IftarTableCommand(Ramadan ramadan) {
        this.ramadan = ramadan;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        if (!player.hasPermission("ramadan.iftar")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Unknown Command Use: /iftar help");
            return true;
        }

        Ramadan plugin = ramadan;

        if (args[0].equalsIgnoreCase("set")) {
            if (args.length != 5) {
                player.sendMessage("Usage: /iftar set <name> <x> <y> <z>");
                return true;
            }

            try {
                String name = args[1];
                double x = Double.parseDouble(args[2]);
                double y = Double.parseDouble(args[3]);
                double z = Double.parseDouble(args[4]);
                Location location = new Location(player.getWorld(), x, y, z);
                plugin.setIftarLocation(name, location);
                player.sendMessage("Iftar location for '" + name + "' set to (" + x + ", " + y + ", " + z + ")");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid coordinates. Usage: /iftar set <name> <x> <y> <z>");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            }

            return true;

        } else if (args[0].equalsIgnoreCase("tp")) {
            if (args.length != 2) {
                player.sendMessage("Usage: /iftar tp <name>");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                return true;
            }

            String name = args[1];
            Location location = ramadan.getIftarLocation(name);

            if (location == null) {
                player.sendMessage("Iftar location for '" + name + "' has not been set.");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                return true;
            }

            player.teleport(location);
            player.sendMessage("Teleported to Iftar location for '" + name + "'.");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            return true;

        } else if (args[0].equalsIgnoreCase("get")) {
            if (args.length != 2) {
                player.sendMessage("Usage: /iftar get <name>");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                return true;
            }

            String name = args[1];
            Location location = plugin.getIftarLocation(name);

            if (location == null) {
                player.sendMessage("Iftar location for '" + name + "' has not been set.");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                return true;
            }

            player.setCompassTarget(location);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

            ItemStack mainHandItem = player.getInventory().getItemInMainHand();

            if (mainHandItem.getType() == Material.COMPASS) {
                ItemMeta compassMeta = mainHandItem.getItemMeta();
                compassMeta.setDisplayName("Iftar Location");
                mainHandItem.setItemMeta(compassMeta);
                player.getInventory().setItemInMainHand(mainHandItem);
            } else {
                player.sendMessage("You need to hold a compass in your main hand to get the location");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            }

            return true;

        } else if (args[0].equalsIgnoreCase("help")) {
            player.sendMessage(ChatColor.GOLD + "----- Iftar Table Help -----");
            player.sendMessage(ChatColor.YELLOW + "/iftar help - Shows this list of commands");
            player.sendMessage(ChatColor.YELLOW + "/iftar set <name> <x> <y> <z> - Sets the location of an iftar spot with the given name at the specified coordinates");
            player.sendMessage(ChatColor.YELLOW + "/iftar tp <name> - Teleports the player to the location of the iftar spot with the given name");
            player.sendMessage(ChatColor.YELLOW + "/iftar get <name> - Sets the player's compass to point towards the location of the iftar spot with the given name");
            return true;
        } else {
            player.sendMessage("You need to hold a compass in your main hand to get the location");
        return true;
        }
    }
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(Arrays.asList("set", "tp", "get", "help"));
        }
        return completions;
    }
}
