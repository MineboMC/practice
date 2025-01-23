package net.minebo.practice.match.postmatchinv.listener;

import net.minebo.practice.Practice;
import net.minebo.practice.match.MatchTeam;
import net.minebo.practice.match.event.MatchCountdownStartEvent;
import net.minebo.practice.match.event.MatchEndEvent;
import net.minebo.practice.match.event.MatchTerminateEvent;
import net.minebo.practice.match.postmatchinv.PostMatchInvHandler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public final class PostMatchInvGeneralListener implements Listener {

    @EventHandler
    public void onMatchTerminate(MatchTerminateEvent event) {
        PostMatchInvHandler postMatchInvHandler = Practice.getInstance().getPostMatchInvHandler();
        postMatchInvHandler.recordMatch(event.getMatch());
    }

    // remove 'old' post match data when their match starts
    @EventHandler
    public void onMatchCountdownStart(MatchCountdownStartEvent event) {
        PostMatchInvHandler postMatchInvHandler = Practice.getInstance().getPostMatchInvHandler();

        for (MatchTeam team : event.getMatch().getTeams()) {
            for (UUID member : team.getAllMembers()) {
                postMatchInvHandler.removePostMatchData(member);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PostMatchInvHandler postMatchInvHandler = Practice.getInstance().getPostMatchInvHandler();
        UUID playerUuid = event.getPlayer().getUniqueId();

        postMatchInvHandler.removePostMatchData(playerUuid);
    }

}