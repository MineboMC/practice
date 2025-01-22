
package net.minebo.practice.scoreboard;

import java.util.*;
import java.util.function.BiConsumer;

import net.minebo.practice.events.Event;
import net.minebo.practice.events.EventHandler;
import net.minebo.practice.events.enums.EventPlayerState;
import net.minebo.practice.events.enums.EventState;
import net.minebo.practice.events.enums.EventType;
import net.minebo.practice.events.games.oitc.OITCHandler;
import net.minebo.practice.events.games.sumo.SumoHandler;
import net.minebo.practice.util.ClickTracker;
import net.minebo.practice.util.TimeUtils;
import net.minebo.practice.tournament.Tournament;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minebo.practice.Practice;
import net.minebo.practice.profile.elo.EloHandler;
import net.minebo.practice.party.Party;
import net.minebo.practice.party.PartyHandler;
import net.minebo.practice.queue.MatchQueue;
import net.minebo.practice.queue.MatchQueueEntry;
import net.minebo.practice.queue.QueueHandler;

final class EventScoreGetter implements BiConsumer<Player, List<String>> {

    @Override
    public void accept(Player player, List<String> scores) {
        EventHandler eventManager = Practice.getInstance().getEventHandler();
        Event event = eventManager.getCurrentEvent();
        scores.add(ChatColor.GRAY + "Event: " + ChatColor.YELLOW + event.type.getName());
        scores.add(ChatColor.GRAY + "Players: " + ChatColor.YELLOW + Event.activePlayers.size() + "/" + event.type.getMaxPlayers());
        if (event.type == EventType.SUMO) {
            scores.add("&b&7&m--------------------");
            Player player1 = SumoHandler.getPlayer1();
            Player player2 = SumoHandler.getPlayer2();
            if (player1 == null || player2 == null) {
                scores.add(ChatColor.YELLOW + "Waiting...");
            } else if (event.playerStates.get(player1.getUniqueId()) == EventPlayerState.FIGHTING && event.playerStates.get(player2.getUniqueId()) == EventPlayerState.FIGHTING) {
                int player1Ping = ((CraftPlayer) player1).getHandle().ping;
                int player2Ping = ((CraftPlayer) player2).getHandle().ping;

                int player1CPS = ClickTracker.getClicks(player1);
                int player2CPS = ClickTracker.getClicks(player2);

                scores.add(ChatColor.YELLOW + player1.getDisplayName() + ChatColor.GRAY + " vs " + ChatColor.YELLOW + player2.getDisplayName());
                scores.add(ChatColor.translate(ChatColor.GRAY + "(" + ChatColor.YELLOW + player1Ping + "ms" + ChatColor.GRAY + ") vs (" + ChatColor.YELLOW + player2Ping + "ms" + ChatColor.GRAY + ")"));
                scores.add(ChatColor.translate(ChatColor.GRAY + "(" + ChatColor.YELLOW + player1CPS + "CPS" + ChatColor.GRAY + ") vs (" + ChatColor.YELLOW + player2CPS + "CPS" + ChatColor.GRAY + ")"));
            } else {
                scores.add(ChatColor.YELLOW + "Waiting...");
            }
        } else if (event.type == EventType.LMS) {
            scores.add("&2&7&m--------------------");
            scores.add(ChatColor.GRAY + "Kit: " + ChatColor.YELLOW + event.getKit().getDisplayName());
        } else if (event.type == EventType.OITC && event.state == EventState.STARTED) {
            scores.add("&3&7&m--------------------");
            Map<Player, Integer> kills = OITCHandler.getKills();

            //LinkedHashMap preserve the ordering of elements in which they are inserted
            LinkedHashMap<Player, Integer> sortedMap = new LinkedHashMap<>();

            kills.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));

            // for loop that executes 5 times
            for (int x = 0; x < 5; x++) {
                if (sortedMap.keySet().stream().skip(x).findFirst().isPresent()) {
                    Player p = sortedMap.keySet().stream().skip(x).findFirst().get();
                    scores.add(ChatColor.GOLD + p.getName() + ": " + ChatColor.WHITE + kills.get(p));
                }
            }

        }
    }

}