package net.minebo.practice.listener;

import net.minebo.basalt.api.BasaltAPI;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class BasicJoinListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        try {
            new ArrayList<>(Arrays.asList("", "Welcome to the " + ChatColor.GOLD + ChatColor.BOLD.toString() + "Minebo Network" + ChatColor.WHITE + ", " + ChatColor.translate(BasaltAPI.INSTANCE.quickFindProfile(event.getPlayer().getUniqueId()).get().getHighestGlobalRank().getColor()) + event.getPlayer().getName() + ChatColor.WHITE + "!", ChatColor.GRAY + ChatColor.ITALIC.toString() + "We are currently in our open practice beta!","")).forEach(event.getPlayer()::sendMessage);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
