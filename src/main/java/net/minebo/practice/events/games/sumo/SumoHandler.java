package net.minebo.practice.events.games.sumo;

import net.minebo.practice.Practice;
import net.minebo.practice.events.Event;
import net.minebo.practice.events.EventHandler;
import net.minebo.practice.events.EventUtils;
import net.minebo.practice.events.enums.EventPlayerState;
import net.minebo.practice.events.enums.EventState;
import net.minebo.practice.util.PatchedPlayerUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.UUID;

public class SumoHandler {
    static EventHandler EventHandler = Practice.getInstance().getEventHandler();
    @Getter @Setter static boolean isOccupied = false;
    @Getter @Setter static boolean canFight = false;

    @Getter static Player player1;
    @Getter static Player player2;

   public static int round = 0;

    public static void startSumoEvent(Event event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (event.state == EventState.INACTIVE) {
                    cancel();
                    return;
                }

                if (event.state == EventState.STARTED && isOccupied == false) {
                    UUID[] players = event.activePlayers.toArray(new UUID[event.activePlayers.size()]);

                    player1 = Bukkit.getPlayer(players[(int) (Math.random() * players.length)]);
                    player2 = Bukkit.getPlayer(players[(int) (Math.random() * players.length)]);

                    while (player1 == player2) {
                        player2 = Bukkit.getPlayer(players[(int) (Math.random() * players.length)]);
                    }

                    initDuel(player1, player2);
                }
            }
        }.runTaskTimer(Practice.getInstance(), 20L, 20L);
    }

    public static void initDuel(Player player1, Player player2) {
        if (isOccupied == true) {
            return;
        }
        Event event = EventHandler.getCurrentEvent();

        canFight = false;
        isOccupied = true;
        round++;

        int[] countdown = { 3 };

        new BukkitRunnable() {
            @Override
            public void run() {
                if (countdown[0] <= 0) {
                    for (UUID uuid : event.getSpectatorsAndPlayers()) {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player != null) {
                            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 2.0F);
                            if (Event.activePlayers.size() == 2) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lFinal Round! " + player1.getDisplayName() + " " + ChatColor.GRAY + "vs " + player2.getDisplayName() + ChatColor.GRAY + "."));
                            } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.GOLD + "[Round " + round + "] " + ChatColor.GRAY + player1.getDisplayName() + ChatColor.GRAY + " vs " + ChatColor.YELLOW + player2.getDisplayName() + ChatColor.GRAY + "."));
                        }
                            }
                    }

                    PatchedPlayerUtils.resetInventory(player1, GameMode.SURVIVAL, true);
                    EventUtils.resetFightingInventory(player1);
                    player1.setVelocity(new Vector(0, 0, 0));
                    Location team1Spawn = EventHandler.getArena().getTeam1Spawn().clone();
                    for(int y = team1Spawn.getBlockY(); y > 0; y--) { // get current Y coordinate, go down until we hit bedrock (0)
                        if(team1Spawn.subtract(0, 1, 0).getBlock().getType() == Material.AIR) continue; // if the block is air, go to next
                        player1.teleport(team1Spawn.add(0, 1, 0)); // teleport player to the block above, otherwise he would be stuck in the current one
                        break; // stop the loop
                    }
                    event.playerStates.put(player1.getUniqueId(), EventPlayerState.FIGHTING);

                    PatchedPlayerUtils.resetInventory(player2, GameMode.SURVIVAL, true);
                    EventUtils.resetFightingInventory(player2);
                    player2.setVelocity(new Vector(0, 0, 0));
                    Location team2Spawn = EventHandler.getArena().getTeam2Spawn().clone();
                    for(int y = team2Spawn.getBlockY(); y > 0; y--) { // get current Y coordinate, go down until we hit bedrock (0)
                        if(team2Spawn.subtract(0, 1, 0).getBlock().getType() == Material.AIR) continue; // if the block is air, go to next
                        player2.teleport(team2Spawn.add(0, 1, 0)); // teleport player to the block above, otherwise he would be stuck in the current one
                        break; // stop the loop
                    }
                    event.playerStates.put(player2.getUniqueId(), EventPlayerState.FIGHTING);

                    canFight = true;

                    this.cancel();
                    return;
                }

                for (UUID uuid : EventHandler.getCurrentEvent().getSpectatorsAndPlayers()) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1.0F);
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.GOLD + "[Round " + round + "] " + ChatColor.GRAY + "Starting in " + ChatColor.YELLOW + countdown[0] + " second" + (countdown[0] == 1 ? "" : "s") + ChatColor.GRAY + "."));
                    }
                }

                countdown[0]--;
            }
        }.runTaskTimer(Practice.getInstance(), 20L, 20L);
    }

    public static void killTheFuckingPlayer(Player player) {
        if (EventHandler.getCurrentEvent() == null) return;
        Event event = EventHandler.getCurrentEvent();
        if (event.playerStates.containsKey(player.getUniqueId()) && event.playerStates.get(player.getUniqueId()) != EventPlayerState.FIGHTING) return;

        EventHandler.removePlayerFromEvent(player);
        Player otherPlayer = player1.getUniqueId() == player.getUniqueId() ? player2 : player1;

        EventHandler.addToWaiting(otherPlayer);
        if (player.isOnline()) {
            EventHandler.addToSpectating(player);
        }

        SumoHandler.player1 = null;
        SumoHandler.player2 = null;
    }
}
