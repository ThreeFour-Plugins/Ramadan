package me.threefour.ramadan.tasks;

import me.threefour.ramadan.events.FastingEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ResetFastingTask implements Runnable {

    private FastingEvent fastingEvent;

    public ResetFastingTask(FastingEvent fastingEvent) {
        this.fastingEvent = fastingEvent;
    }

    @Override
    public void run() {
        // Remove fasting status from all players
        for (Player player : Bukkit.getOnlinePlayers()) {
            fastingEvent.removeFastingPlayer(player);
        }

        // Stop fasting
        fastingEvent.stopFasting();

        // Reset fasting time
        fastingEvent.resetFastingTime();
    }
}
