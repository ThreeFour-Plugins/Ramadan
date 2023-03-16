package me.threefour.ramadan.commands;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class DonateCommand implements CommandExecutor {
    private Economy economy;

    public DonateCommand() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            Bukkit.getLogger().warning("Could not find a Vault-compatible economy plugin. Disabling /donate command.");
            return;
        }
        this.economy = rsp.getProvider();
    }

    public DonateCommand(Economy economy) {
        this.economy = economy;
    }

    private boolean hasEconomy() {
        return (this.economy != null);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        double amount;
        if (!(sender instanceof Player player)) {
            sender.sendMessage("" + ChatColor.RED + "Only players can use this command.");
            return true;
        }
        if (!hasEconomy()) {
            player.sendMessage("" + ChatColor.RED + "Economy plugin not found. Please try again later.");
            return true;
        }
        if (args.length < 2) {
            player.sendMessage("" + ChatColor.RED + "Usage: /donate <player> <amount>");
            return true;
        }
        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        if (!target.hasPlayedBefore()) {
            player.sendMessage("" + ChatColor.RED + "Player not found: " + ChatColor.RED);
            return true;
        }
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("" + ChatColor.RED + "Invalid amount: " + ChatColor.RED);
            return true;
        }
        if (amount < 0.0D) {
            player.sendMessage("" + ChatColor.RED + "Amount cannot be negative.");
            return true;
        }
        if (!this.economy.has((OfflinePlayer)player, amount)) {
            player.sendMessage("" + ChatColor.RED + "You don't have enough money to make this donation.");
            return true;
        }
        this.economy.withdrawPlayer((OfflinePlayer)player, amount);
        this.economy.depositPlayer(target, amount);
        player.sendMessage("" + ChatColor.GREEN + "You donated " + ChatColor.GREEN + " to " + this.economy.format(amount) + ".");
        return true;
    }
}
