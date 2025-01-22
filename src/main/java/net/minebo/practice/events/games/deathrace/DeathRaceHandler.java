package net.minebo.practice.events.games.deathrace;

import net.minebo.practice.Practice;
import net.minebo.practice.events.Event;
import net.minebo.practice.events.EventHandler;
import net.minebo.practice.events.enums.EventPlayerState;
import net.minebo.practice.events.enums.EventState;
import net.minebo.practice.kit.kittype.KitType;
import net.minebo.practice.util.InventoryUtils;
import net.minebo.practice.util.PatchedPlayerUtils;
import net.minebo.practice.util.PlayerUtils;
import net.minebo.practice.util.VisibilityUtils;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class DeathRaceHandler {
    static EventHandler EventHandler = Practice.getInstance().getEventHandler();
    @Getter
    @Setter
    static boolean canFight = false;

    public static void startDeathRaceEvent(Event event) {
        if (event.state == EventState.INACTIVE) {
            return;
        }

        canFight = false;
        KitType kit = KitType.byId("SOUP");

        if (event.state == EventState.STARTED) {
            for (UUID uuid : event.activePlayers) {
                Player player = Bukkit.getPlayer(uuid);

                if (event.playerStates.get(uuid) == EventPlayerState.WAITING) {
                    event.playerStates.put(uuid, EventPlayerState.FIGHTING);
                    player.getInventory().setContents(kit.getDefaultInventory());
                    player.getInventory().setArmorContents(kit.getDefaultArmor());
                    player.teleport(EventHandler.getArena().getEventSpawns().get(0));
                    PatchedPlayerUtils.denyMovement(player);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1));
                }
            }

            int[] countdown = { 10 };

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (countdown[0] <= 0) {
                        canFight = true;
                        event.getSpectatorsAndPlayers().forEach(uuidNew -> {
                            Player player = Bukkit.getPlayer(uuidNew);
                            if (player != null) {
                                PatchedPlayerUtils.allowMovement(player);
                                player.removePotionEffect(PotionEffectType.BLINDNESS);
                                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 2.0F);
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Death Race&7] &eGO!"));
                            }
                        });
                        this.cancel();
                        return;
                    }
                    event.getSpectatorsAndPlayers().forEach(uuidNew -> {
                        Player player = Bukkit.getPlayer(uuidNew);
                        if (player != null) {
                            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1.0F);
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6Death Race&7] &e" + countdown[0] + "..."));
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

        PatchedPlayerUtils.resetInventory(player, GameMode.SURVIVAL, true);
        InventoryUtils.resetInventoryDelayed(player);
        VisibilityUtils.updateVisibility(player);

        PlayerUtils.animateDeath(player, true);

        EventHandler.removePlayerFromEvent(player);

        if (player.isOnline()) {
            EventHandler.addToSpectating(player);
            PatchedPlayerUtils.allowMovement(player);
        }
    }
}

