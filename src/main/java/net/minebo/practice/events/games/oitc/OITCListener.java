package net.minebo.practice.events.games.oitc;

import net.minebo.practice.Practice;
import net.minebo.practice.events.Event;
import net.minebo.practice.events.EventHandler;
import net.minebo.practice.events.enums.EventType;
import net.minebo.practice.util.PatchedPlayerUtils;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static net.minebo.practice.events.games.oitc.OITCHandler.getKills;

public class OITCListener implements Listener {
    public static Set<Player> doubleJumpCooldown = new HashSet<>();
    public static Set<Player> canSlam = new HashSet<>();

    @org.bukkit.event.EventHandler
    public void onFallDamage(EntityDamageEvent e) {
        Event currentEvent = EventHandler.getCurrentEvent();
        if (currentEvent == null) return;
        if (currentEvent.getType() != EventType.OITC) return;
        if (!currentEvent.isPlayerInEvent(e.getEntity().getUniqueId())) return;

        if (e.getEntity() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            e.setCancelled(true);
        }
    }

    @org.bukkit.event.EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Event currentEvent = EventHandler.getCurrentEvent();
        if (currentEvent == null) return;
        if (currentEvent.getType() != EventType.OITC) return;
        if (!currentEvent.isPlayerInEvent(e.getPlayer().getUniqueId())) return;

        e.setCancelled(true);
    }

    @org.bukkit.event.EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Event currentEvent = EventHandler.getCurrentEvent();
        if (currentEvent == null) return;
        if (currentEvent.getType() != EventType.OITC) return;
        if (!currentEvent.isPlayerInEvent(e.getPlayer().getUniqueId())) return;

        OITCHandler.kills.remove(e.getPlayer());
        canSlam.remove(e.getPlayer());
    }

    @org.bukkit.event.EventHandler
    public void onDamageEvent(EntityDamageByEntityEvent e) {
        Event currentEvent = EventHandler.getCurrentEvent();
        if (currentEvent == null) return;
        if (currentEvent.getType() != EventType.OITC) return;

        if (e.getDamager() instanceof Arrow && e.getEntity() instanceof Player && currentEvent.isPlayerInEvent(e.getEntity().getUniqueId())) {
            Player shooter = (Player) ((Arrow)e.getDamager()).getShooter();
            if (shooter != e.getEntity()) {
                e.setDamage(200);
                shooter.setHealth(20);
            }
        }
    }

    @org.bukkit.event.EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent e) {
        Event currentEvent = EventHandler.getCurrentEvent();
        if (currentEvent == null) return;
        if (currentEvent.getType() != EventType.OITC) return;
        if (!currentEvent.isPlayerInEvent(e.getEntity().getUniqueId())) return;

        Player player = e.getEntity();
        int playerKills = getKills(player);
        Player killer = player.getKiller();

        canSlam.remove(player);
        player.setItemInHand(null);
        e.getDrops().clear();

        if (killer != null) {
            int killerKills = getKills(killer) + 1;
            EventHandler.messageAll("&c[" + killerKills + "] &6" + PatchedPlayerUtils.getFormattedName(killer.getUniqueId()) + "&e has killed &c[" + playerKills + "] &6" + PatchedPlayerUtils.getFormattedName(player.getUniqueId()) + "&e.");

            if (killerKills >= OITCHandler.killsToWin) {
                EventHandler.messageAll("&6" + killer.getName() + "&e has won the event.");
                player.setHealth(20);
                EventHandler.endEvent(killer);
                doubleJumpCooldown = new HashSet<>();
                canSlam = new HashSet<>();
                OITCHandler.kills = new HashMap<>();
                return;
            }
            OITCHandler.killTheFuckingPlayer(player, killer);
        } else {
            EventHandler.messageAll("&c[" + playerKills + "] &6" + PatchedPlayerUtils.getFormattedName(player.getUniqueId()) + "&c died.");
            OITCHandler.killTheFuckingPlayer(player, null);
        }
    }


    // durability change event
    @org.bukkit.event.EventHandler
    public void onPlayerItemDamageEvent(PlayerItemDamageEvent e) {
        Event currentEvent = EventHandler.getCurrentEvent();
        if (currentEvent == null) return;
        if (currentEvent.getType() != EventType.OITC) return;
        if (!currentEvent.isPlayerInEvent(e.getPlayer().getUniqueId())) return;

        e.setCancelled(true);
        Bukkit.getScheduler().runTaskLater(Practice.getInstance(), e.getPlayer()::updateInventory, 1L);
    }


    // double jump event
    @org.bukkit.event.EventHandler
    public void onJump(PlayerToggleFlightEvent event) {
        Event currentEvent = EventHandler.getCurrentEvent();
        if (currentEvent == null) return;
        if (currentEvent.getType() != EventType.OITC) return;
        if (!currentEvent.isPlayerInEvent(event.getPlayer().getUniqueId())) return;
        Player player = event.getPlayer();
        if (doubleJumpCooldown.contains(player)) return;

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        event.setCancelled(true);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setVelocity(player.getLocation().getDirection().normalize().multiply(2).setY(1));
        player.playSound(player.getLocation(), Sound.EXPLODE, 1, 10);
        canSlam.remove(player);
        doubleJumpCooldown.add(player);
        canSlam.add(player);
        int[] doubleJumpTimer = { 5 };
        player.setLevel(doubleJumpTimer[0]);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!doubleJumpCooldown.contains(player)) {
                    cancel();
                }

                if (doubleJumpTimer[0] <= 0) {
                    player.setLevel(0);
                    player.setAllowFlight(true);
                    player.sendMessage(ChatColor.translate("&eYou may now use &dBoost Jump&e."));
                    canSlam.remove(player);
                    doubleJumpCooldown.remove(player);
                    cancel();
                    return;
                }
                player.setLevel(doubleJumpTimer[0]);

                doubleJumpTimer[0]--;
            }
        }.runTaskTimer(Practice.getInstance(), 20L, 20L);
    }

    @org.bukkit.event.EventHandler
    public void onPlayerGround(PlayerMoveEvent event) {
        Event currentEvent = EventHandler.getCurrentEvent();
        if (currentEvent == null) return;
        if (currentEvent.getType() != EventType.OITC) return;
        if (!currentEvent.isPlayerInEvent(event.getPlayer().getUniqueId())) return;
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR) {
            canSlam.remove(player);
        }
    }

    @org.bukkit.event.EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Event currentEvent = EventHandler.getCurrentEvent();
        if (currentEvent == null) return;
        if (currentEvent.getType() != EventType.OITC) return;
        if (!currentEvent.isPlayerInEvent(event.getPlayer().getUniqueId())) return;
        Player player = event.getPlayer();

        if (canSlam.contains(player)) {
            player.setVelocity(player.getLocation().getDirection().multiply(4));
            player.playSound(player.getLocation(), Sound.EXPLODE, 1, 10);
            canSlam.remove(player);
        }
    }

}
