package me.threefour.ramadan.commands;

import me.threefour.ramadan.events.FastingEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StopFastingCommand implements CommandExecutor {

    private final FastingEvent fastingEvent;

    public StopFastingCommand(FastingEvent fastingEvent) {
        this.fastingEvent = fastingEvent;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("stopfasting")) {
            if (!sender.hasPermission("ramadan.stopfasting")) {
                sender.sendMessage("You don't have permission to use this command.");
                return true;
            }
            fastingEvent.stopFasting();
            sender.sendMessage("Fasting stopped!");
            return true;
        }
        return false;
    }
}
