package net.minebo.practice.misc;

import lombok.Getter;
import net.minebo.practice.Practice;
import org.bukkit.Bukkit;

@Getter
public class Cache implements Runnable {

    private int onlineCount = 0;
    private int fightsCount = 0;
    private int queuesCount = 0;

    @Override
    public void run() {
        onlineCount = Bukkit.getOnlinePlayers().size();
        fightsCount = Practice.getInstance().getMatchHandler().countPlayersPlayingInProgressMatches();
        queuesCount = Practice.getInstance().getQueueHandler().getQueuedCount();
    }

}
