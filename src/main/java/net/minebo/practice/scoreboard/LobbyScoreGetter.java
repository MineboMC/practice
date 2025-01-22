
package net.minebo.practice.scoreboard;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

import net.minebo.practice.util.TimeUtils;
import net.minebo.practice.tournament.Tournament;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.minebo.practice.Practice;
import net.minebo.practice.profile.elo.EloHandler;
import net.minebo.practice.party.Party;
import net.minebo.practice.party.PartyHandler;
import net.minebo.practice.queue.MatchQueue;
import net.minebo.practice.queue.MatchQueueEntry;
import net.minebo.practice.queue.QueueHandler;

final class LobbyScoreGetter implements BiConsumer<Player, List<String>> {

    @Override
    public void accept(Player player, List<String> scores) {
        Optional<UUID> followingOpt = Practice.getInstance().getFollowHandler().getFollowing(player);
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        EloHandler eloHandler = Practice.getInstance().getEloHandler();

        Party playerParty = partyHandler.getParty(player);
        MatchQueueEntry entry = getQueueEntry(player);
        Tournament tournament = Practice.getInstance().getTournamentHandler().getTournament();

        scores.add("&fOnline: &e" + Practice.getInstance().getCache().getOnlineCount());
        scores.add("&fFighting: &e" + Practice.getInstance().getCache().getFightsCount());
        scores.add("&fQueueing: &e" + Practice.getInstance().getCache().getQueuesCount());
        
        // this definitely can be a .ifPresent, however creating the new lambda that often
        // was causing some performance issues, so we do this less pretty (but more efficient)
        // check (we can't define the lambda up top and reference because we reference the
        // scores variable)
        if (followingOpt.isPresent()) {
            Player following = Bukkit.getPlayer(followingOpt.get());
            scores.add("&fFollowing: &e" + following.getName());

            if (player.hasPermission("potpvp.silent")) {
                MatchQueueEntry targetEntry = getQueueEntry(following);

                if (targetEntry != null) {
                    MatchQueue queue = targetEntry.getQueue();
                    scores.add("&fTarget Queue: &c" + (queue.isRanked() ? "Ranked" : "&aUnranked") + " " + queue.getKitType().getDisplayName());
                }
            }
        } else if (entry != null) {
            String waitTimeFormatted = TimeUtils.formatIntoMMSS(entry.getWaitSeconds());
            MatchQueue queue = entry.getQueue();

            scores.add("");
            scores.add("&6&lQueuing");
            scores.add(" &fType: &c" + (queue.isRanked() ? "Ranked" : "&aUnranked"));
            scores.add(" &fLadder: " + queue.getKitType().getDisplayColor() + queue.getKitType().getDisplayName());
            scores.add(" &fTime: &e" + waitTimeFormatted);
            if (queue.isRanked()) {
                int elo = eloHandler.getElo(entry.getMembers(), queue.getKitType());
                int window = entry.getWaitSeconds() * QueueHandler.RANKED_WINDOW_GROWTH_PER_SECOND;
                scores.add(ChatColor.WHITE + " &fELO Range: " + ChatColor.YELLOW + Math.max(0, (elo - window)) + " - " + (elo + window));
            }
        } else if (tournament != null) {
            scores.add("");
            scores.add("&6&lTournament:");

            if (tournament.getStage() == Tournament.TournamentStage.WAITING_FOR_TEAMS) {
                int teamSize = tournament.getRequiredPartySize();
                scores.add(" &fKit&7: &e" + tournament.getType().getDisplayName());
                scores.add(" &fTeam Size&7: " + teamSize + "v" + teamSize);
                int multiplier = teamSize < 3 ? teamSize : 1;
                scores.add("&f" + (teamSize < 3 ? "Players"  : "Teams") + "&7: &e" + (tournament.getActiveParties().size() * multiplier + "/" + tournament.getRequiredPartiesToStart() * multiplier));
            } else if (tournament.getStage() == Tournament.TournamentStage.COUNTDOWN) {
                if (tournament.getCurrentRound() == 0) {
                    scores.add("");
                    scores.add(" &7Begins in &e" + tournament.getBeginNextRoundIn() + "&7 second" + (tournament.getBeginNextRoundIn() == 1 ? "." : "s."));
                } else {
                    scores.add("");
                    scores.add(" &6Round " + ChatColor.YELLOW + (tournament.getCurrentRound() + 1));
                    scores.add(" &7Begins in &e" + tournament.getBeginNextRoundIn() + "&7 second" + (tournament.getBeginNextRoundIn() == 1 ? "." : "s."));
                }
            } else if (tournament.getStage() == Tournament.TournamentStage.IN_PROGRESS) {
                scores.add(" &fRound&7: " + ChatColor.YELLOW + tournament.getCurrentRound());

                int teamSize = tournament.getRequiredPartySize();
                int multiplier = teamSize < 3 ? teamSize : 1;

                scores.add("&f " + (teamSize < 3 ? "Players" : "Teams") + "&7: &e" + tournament.getActiveParties().size() * multiplier + "/" + tournament.getRequiredPartiesToStart() * multiplier);
                scores.add("&f Duration: &e" + TimeUtils.formatIntoMMSS((int) (System.currentTimeMillis() - tournament.getRoundStartedAt()) / 1000));
            }
        } else if (playerParty != null) {
            scores.add("");
            scores.add("&6&lParty: ");
            scores.add("&f Leader: " + ChatColor.YELLOW + Bukkit.getPlayer(playerParty.getLeader()).getDisplayName());
            scores.add("&f Members: " + ChatColor.YELLOW + playerParty.getMembers().size() + "/" + Party.MAX_SIZE);
        }
    }

    private MatchQueueEntry getQueueEntry(Player player) {
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        QueueHandler queueHandler = Practice.getInstance().getQueueHandler();

        Party playerParty = partyHandler.getParty(player);
        if (playerParty != null) return queueHandler.getQueueEntry(playerParty);

        return queueHandler.getQueueEntry(player.getUniqueId());
    }

}