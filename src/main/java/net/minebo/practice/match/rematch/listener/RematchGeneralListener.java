package net.minebo.practice.match.rematch.listener;

import net.minebo.practice.Practice;
import net.minebo.practice.match.event.MatchTerminateEvent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class RematchGeneralListener implements Listener {

    @EventHandler
    public void onMatchTerminate(MatchTerminateEvent event) {
        Practice.getInstance().getRematchHandler().registerRematches(event.getMatch());
    }

}