package net.minebo.practice.events.games.sumo;

import net.minebo.practice.Practice;
import net.minebo.practice.events.Event;
import net.minebo.practice.events.EventHandler;
import net.minebo.practice.events.enums.EventPlayerState;
import net.minebo.practice.events.enums.EventType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SumoListener implements Listener {
    EventHandler EventHandler = Practice.getInstance().getEventHandler();

    @org.bukkit.event.EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Event currentEvent = EventHandler.getCurrentEvent();
        if (currentEvent == null || currentEvent.getType() != EventType.SUMO) return;
        Player player = e.getPlayer();
        if (currentEvent.isPlayerInEvent(player.getUniqueId()) && currentEvent.playerStates.get(player.getUniqueId()) == EventPlayerState.FIGHTING) {
            if (EventHandler.getArena().getTeam1Spawn().getY() - 5 > player.getLocation().getY()) {
                EventHandler.getCurrentEvent().getSpectatorsAndPlayers().forEach(uuid -> {
                    Player player1 = Bukkit.getPlayer(uuid);
                    if (player1 != null) {
                        player1.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.DARK_RED + player.getName() + "&c has been eliminated."));
                    }
                });
                SumoHandler.setOccupied(false);
                SumoHandler.killTheFuckingPlayer(player);
            }
        }
    }

    @org.bukkit.event.EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Event currentEvent = EventHandler.getCurrentEvent();
            Player victim = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();
            if (currentEvent == null || currentEvent.getType() != EventType.SUMO) return;
                if (currentEvent.isPlayerInEvent(victim.getUniqueId()) && currentEvent.playerStates.get(victim.getUniqueId()) == EventPlayerState.FIGHTING) {
                    event.setDamage(0);
                }
                if (currentEvent.isPlayerInEvent(damager.getUniqueId()) && currentEvent.playerStates.get(damager.getUniqueId()) == EventPlayerState.FIGHTING) {
                    event.setDamage(0);
                }
        }
    }

    @org.bukkit.event.EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Event currentEvent = EventHandler.getCurrentEvent();
        if (currentEvent == null || currentEvent.getType() != EventType.SUMO) return;
        if (currentEvent.getSpectatorsAndPlayers().contains(player.getUniqueId())) {
            System.out.println(currentEvent.playerStates.get(player.getUniqueId()));
            if (currentEvent.playerStates.get(player.getUniqueId()) == EventPlayerState.FIGHTING) {
                EventHandler.getCurrentEvent().getSpectatorsAndPlayers().forEach(uuid -> {
                    Player player1 = Bukkit.getPlayer(uuid);
                    if (player1 != null) {
                        player1.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.DARK_RED + player.getName() + "&c has been eliminated."));
                    }
                });
                SumoHandler.setOccupied(false);
                SumoHandler.killTheFuckingPlayer(player);
            } else {
                EventHandler.removePlayerFromEvent(player);
            }
        }
    }
}
