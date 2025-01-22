package net.minebo.practice.events.games.lms;

import net.minebo.practice.Practice;
import net.minebo.practice.events.Event;
import net.minebo.practice.events.EventHandler;
import net.minebo.practice.events.enums.EventPlayerState;
import net.minebo.practice.events.enums.EventState;
import net.minebo.practice.util.InventoryUtils;
import net.minebo.practice.util.PatchedPlayerUtils;
import net.minebo.practice.util.PlayerUtils;
import net.minebo.practice.util.VisibilityUtils;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.UUID;

public class LMSHandler {
    static EventHandler EventHandler = Practice.getInstance().getEventHandler();
    @Getter @Setter static boolean canFight = false;

    public static void startLMSEvent(Event event) {
        if (event.state == EventState.INACTIVE || event.getKit() == null) {
            return;
        }

        canFight = false;

        if (event.state == EventState.STARTED) {
            event.activePlayers.forEach(p -> {
                Player player = Bukkit.getPlayer(p);

                if (event.playerStates.get(p) == EventPlayerState.WAITING) {
                    player.setWalkSpeed(0.0f);
                    event.playerStates.put(p, EventPlayerState.FIGHTING);
                    player.getInventory().setContents(event.getKit().getDefaultInventory());
                    player.getInventory().setArmorContents(event.getKit().getDefaultArmor());
                    player.teleport(net.minebo.practice.events.EventHandler.getArena().getEventSpawns().get(new Random().nextInt(net.minebo.practice.events.EventHandler.getArena().getEventSpawns().size()-1)));
                }

            });

            int[] countdown = { 10 };

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (countdown[0] <= 0) {
                        canFight = true;
                        event.activePlayers.forEach(p->Bukkit.getPlayer(p).setWalkSpeed(0.2f));
                        event.getSpectatorsAndPlayers().forEach(uuidNew -> {
                            Player player = Bukkit.getPlayer(uuidNew);
                            if (player != null) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6LMS&7] &ePvP is now enabled."));
                            }
                        });
                        this.cancel();
                        return;
                    }
                    event.getSpectatorsAndPlayers().forEach(uuidNew -> {
                        Player player = Bukkit.getPlayer(uuidNew);
                        if (player != null) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6LMS&7] &eStarting in &6" + countdown[0] + "&e second" + (countdown[0] == 1 ? "" : "s") + "."));
                        }
                    });
                    countdown[0]--;
                }
            }.runTaskTimer(Practice.getInstance(), 20, 20);
        }
    }

    public static void killTheFuckingPlayer(Player player) {
        if (EventHandler.getCurrentEvent() == null) return;
        Event event = EventHandler.getCurrentEvent();
        if (event.playerStates.containsKey(player.getUniqueId()) && event.playerStates.get(player.getUniqueId()) != EventPlayerState.FIGHTING) return;

        player.setFireTicks(0);

        player.teleport(player.getLocation().add(0, 2, 0));
        event.playerStates.put(player.getUniqueId(), EventPlayerState.DEAD);

        PatchedPlayerUtils.resetInventory(player, GameMode.SURVIVAL, true);
        InventoryUtils.resetInventoryDelayed(player);
        VisibilityUtils.updateVisibility(player);

        PlayerUtils.animateDeath(player, true);

        EventHandler.removePlayerFromEvent(player);

        if (player.isOnline()) {
            EventHandler.addToSpectating(player);
        }
    }
}
