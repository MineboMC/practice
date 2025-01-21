package net.minebo.practice.match.rematch.listener;

import net.minebo.practice.Practice;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public final class RematchUnloadListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Practice.getInstance().getRematchHandler().unloadRematchData(event.getPlayer());
    }

}