package me.threefour.ramadan;

import me.threefour.ramadan.commands.DonateCommand;
import me.threefour.ramadan.commands.ReloadCommand;
import me.threefour.ramadan.events.RamadanBedEvent;
import me.threefour.ramadan.events.RamadanEvent;
import me.threefour.ramadan.listeners.RamadanListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import me.threefour.ramadan.update.GithubUpdate;

import java.util.Objects;

public class Ramadan extends JavaPlugin implements Listener {

    private Economy economy;

    @Override
    public void onEnable() {
        getLogger().info("Ramadan has been enabled!");
        // Initialize update checker
        GithubUpdate updateChecker = new GithubUpdate(this, "ThreeFour-Plugins/Ramadan", getDescription().getVersion());
        updateChecker.checkForUpdates();
        checkEconomy();
        // Register events
        getServer().getPluginManager().registerEvents(new RamadanEvent(this), this);
        getServer().getPluginManager().registerEvents(new RamadanBedEvent(), this);
        getServer().getPluginManager().registerEvents(new RamadanListener(), this);

        // Reload config.yml
        reloadConfig();
        saveDefaultConfig();

        // Register commands
        getCommand("donate").setExecutor(new DonateCommand(this.economy));
        Objects.requireNonNull(getCommand("ramadan")).setExecutor(new ReloadCommand(this));
    }

    public void onDisable() {
        getLogger().info("Ramadan has been disabled!");
        saveDefaultConfig();
    }

    private void checkEconomy() {
        if (!getServer().getPluginManager().isPluginEnabled("Vault")) {
            getLogger().warning("Vault not found, economy support disabled!");
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().warning("Could not find an economy plugin, economy support disabled!");
            return;
        }
        economy = rsp.getProvider();
        getLogger().info("Economy support enabled!");
    }
}
