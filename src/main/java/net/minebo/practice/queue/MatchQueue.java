package net.minebo.practice.queue;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import net.minebo.practice.Practice;
import net.minebo.practice.profile.elo.EloHandler;
import net.minebo.practice.kit.kittype.KitType;
import net.minebo.practice.match.Match;
import net.minebo.practice.match.MatchHandler;
import net.minebo.practice.match.MatchTeam;
import net.minebo.practice.util.PatchedPlayerUtils;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.Getter;

public final class MatchQueue {

    @Getter private final KitType kitType;
    @Getter private final boolean ranked;
    private final List<MatchQueueEntry> entries = new CopyOnWriteArrayList<>();

    MatchQueue(KitType kitType, boolean ranked) {
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
        this.ranked = ranked;
    }

    void tick() {
        // we clone so we can remove entries from our working set
        // (sometimes matches fail to create [ex no maps open] and
        // we should retry)
        List<MatchQueueEntry> entriesCopy = new ArrayList<>(entries);
        EloHandler eloHandler = Practice.getInstance().getEloHandler();

        // ranked match algorithm requires entries are in
        // order by elo. There's no reason we only do this for ranked
        // matches aside from performance
        if (ranked) {
            entriesCopy.sort(Comparator.comparing(e -> eloHandler.getElo(e.getMembers(), kitType)));
        }

        while (entriesCopy.size() >= 2) {
            // remove from 0 both times because index shifts down
            MatchQueueEntry a = entriesCopy.remove(0);
            MatchQueueEntry b = entriesCopy.remove(0);

            // the algorithm for ranked and unranked queues is actually very similar,
            // except for the fact ranked matches can't be made if the elo window for
            // both players don't overlap
            if (ranked) {
                int aElo = eloHandler.getElo(a.getMembers(), kitType);
                int bElo = eloHandler.getElo(b.getMembers(), kitType);

                int aEloWindow = a.getWaitSeconds() * QueueHandler.RANKED_WINDOW_GROWTH_PER_SECOND;
                int bEloWindow = b.getWaitSeconds() * QueueHandler.RANKED_WINDOW_GROWTH_PER_SECOND;

                if (Math.abs(aElo - bElo) > Math.max(aEloWindow, bEloWindow)) {
                    continue;
                }
            }

            createMatchAndRemoveEntries(a, b);
        }
    }

    public int countPlayersQueued() {
        int count = 0;

        for (MatchQueueEntry entry : entries) {
            count += entry.getMembers().size();
        }

        return count;
    }

    void addToQueue(MatchQueueEntry entry) {
        entries.add(entry);
    }

    void removeFromQueue(MatchQueueEntry entry) {
        entries.remove(entry);
    }

    private void createMatchAndRemoveEntries(MatchQueueEntry entryA, MatchQueueEntry entryB) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        QueueHandler queueHandler = Practice.getInstance().getQueueHandler();

        MatchTeam teamA = new MatchTeam(entryA.getMembers());
        MatchTeam teamB = new MatchTeam(entryB.getMembers());

        Match match = matchHandler.startMatch(
            ImmutableList.of(teamA, teamB),
            kitType,
            ranked,
            !ranked,
           null
        );

        // only remove entries if match creation was successfull
        if (match != null) {
            queueHandler.removeFromQueueCache(entryA);
            queueHandler.removeFromQueueCache(entryB);

            String teamAElo = "";
            String teamBElo = "";

            if (ranked) {
                EloHandler eloHandler = Practice.getInstance().getEloHandler();

                teamAElo = " (" + eloHandler.getElo(teamA.getAliveMembers(), kitType) + " Elo)";
                teamBElo = " (" + eloHandler.getElo(teamB.getAliveMembers(), kitType) + " Elo)";
            }

            String foundStart = ChatColor.GREEN.toString() + ChatColor.BOLD + "Match found!";

            teamA.messageAlive("");
            teamA.messageAlive(ChatColor.GREEN + ChatColor.BOLD.toString() + "Match Found!");
            teamA.messageAlive("");
            teamA.messageAlive("Kit: " + kitType.getColoredDisplayName());
            teamA.messageAlive("Opponent: " + Joiner.on(", ").join(PatchedPlayerUtils.mapToDisplayNames(teamB.getAllMembers())));
            teamA.messageAlive("Ping: " + ChatColor.AQUA + Joiner.on(", ").join(PatchedPlayerUtils.mapToPings(teamB.getAllMembers())));
            teamA.messageAlive("");

            teamB.messageAlive("");
            teamB.messageAlive(ChatColor.GREEN + ChatColor.BOLD.toString() + "Match Found!");
            teamB.messageAlive("");
            teamB.messageAlive("Kit: " + kitType.getColoredDisplayName());
            teamB.messageAlive("Opponent: " + Joiner.on(", ").join(PatchedPlayerUtils.mapToDisplayNames(teamA.getAllMembers())));
            teamB.messageAlive("Ping: " + ChatColor.AQUA + Joiner.on(", ").join(PatchedPlayerUtils.mapToPings(teamA.getAllMembers())));
            teamB.messageAlive("");

            entries.remove(entryA);
            entries.remove(entryB);
        }
    }

}