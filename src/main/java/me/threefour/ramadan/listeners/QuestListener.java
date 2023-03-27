package me.threefour.ramadan.listeners;

import me.threefour.ramadan.Ramadan;
import me.threefour.ramadan.events.QuestCompleteEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class QuestListener implements Listener {

    private Ramadan plugin;

    public QuestListener(Ramadan ramadan) {
        this.plugin = ramadan;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        List<String> completedQuests = plugin.getCompletedQuests(player);

        for (String quest : completedQuests) {
            player.sendMessage(ChatColor.GREEN + "You have completed the " + quest + " quest!");
        }
    }

    @EventHandler
    public void onQuestComplete(QuestCompleteEvent event) {
        Player player = event.getPlayer();
        String quest = event.getQuest();
        List<String> completedQuests = plugin.getCompletedQuests(player);

        if (!completedQuests.contains(quest)) {
            player.sendMessage(ChatColor.RED + "You have not completed the " + quest + " quest!");
            return;
        }

        if (!plugin.getQuestRewards().containsKey(quest)) {
            player.sendMessage(ChatColor.RED + "Reward not found for quest: " + quest);
            return;
        }

        String reward = plugin.getQuestRewards().getString(quest);
        player.sendMessage(ChatColor.GREEN + "You have completed the " + quest + " quest and earned " + reward + "!");
    }
}
