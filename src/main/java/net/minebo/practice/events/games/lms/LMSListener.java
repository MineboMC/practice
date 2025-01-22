package net.minebo.practice.events.games.lms;

import net.minebo.practice.Practice;
import net.minebo.practice.events.Event;
import net.minebo.practice.events.EventHandler;
import net.minebo.practice.events.enums.EventPlayerState;
import net.minebo.practice.events.enums.EventType;
import net.minebo.practice.kit.kittype.KitType;
import net.minebo.practice.util.PatchedPlayerUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class LMSListener implements Listener {
    EventHandler EventHandler = Practice.getInstance().getEventHandler();

    @org.bukkit.event.EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Event currentEvent = EventHandler.getCurrentEvent();
        if (currentEvent == null) return;
        if (currentEvent.getType() != EventType.LMS) return;

        Player player = event.getEntity();
        Player killer = player.getKiller();

        for (ItemStack item : event.getDrops()) {
            if (item.getType() == Material.POTION || item.getType() == Material.MUSHROOM_SOUP) {
                player.getWorld().dropItem(player.getLocation(), item);
            }
        }
        event.getDrops().clear();

        if (currentEvent.playerStates.get(player.getUniqueId()) == EventPlayerState.FIGHTING) {
            EventHandler.getCurrentEvent().getSpectatorsAndPlayers().forEach(uuid -> {
                Player player1 = Bukkit.getPlayer(uuid);
                if (player1 != null) {
                    if (killer != null) {
                        player1.sendMessage(ChatColor.translateAlternateColorCodes('&', PatchedPlayerUtils.getFormattedName(player.getUniqueId()) + "&e has been eliminated by "+PatchedPlayerUtils.getFormattedName(killer.getUniqueId())  +"&e."));
                    } else {
                        player1.sendMessage(ChatColor.translateAlternateColorCodes('&', PatchedPlayerUtils.getFormattedName(player.getUniqueId()) + "&c has been eliminated."));
                    }
                }
            });

            LMSHandler.killTheFuckingPlayer(player);
        }
    }

    @org.bukkit.event.EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Event currentEvent = EventHandler.getCurrentEvent();
            if (currentEvent == null) return;
            if (currentEvent.getType() != EventType.LMS) return;

            Player victim = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();

            if (!currentEvent.getSpectatorsAndPlayers().contains(victim.getUniqueId())) return;
            if (!currentEvent.getSpectatorsAndPlayers().contains(damager.getUniqueId())) return;

            if (!LMSHandler.isCanFight()) {
                event.setCancelled(true);
                return;
            }

            if (currentEvent.isPlayerInEvent(victim.getUniqueId()) && currentEvent.playerStates.get(victim.getUniqueId()) != EventPlayerState.FIGHTING && currentEvent.isPlayerInEvent(damager.getUniqueId()) && currentEvent.playerStates.get(damager.getUniqueId()) != EventPlayerState.FIGHTING) {
                event.setCancelled(true);
            }
        }
    }

    @org.bukkit.event.EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Event currentEvent = EventHandler.getCurrentEvent();
        if (currentEvent == null) return;
        if (currentEvent.getType() != EventType.LMS) return;


        if (currentEvent.getSpectatorsAndPlayers().contains(player.getUniqueId())) {
            if (currentEvent.playerStates.get(player.getUniqueId()) == EventPlayerState.FIGHTING) {
                for (ItemStack item : player.getInventory().getContents()) {
                    if (item != null && item.getType() != null) {
                        if (item.getType() == Material.POTION || item.getType() == Material.MUSHROOM_SOUP) {
                            player.getWorld().dropItem(player.getLocation(), item);
                        }
                    }
                }
                EventHandler.getCurrentEvent().getSpectatorsAndPlayers().forEach(uuid -> {
                    Player player1 = Bukkit.getPlayer(uuid);
                    if (player1 != null) {
                        player1.sendMessage(ChatColor.translateAlternateColorCodes('&', PatchedPlayerUtils.getFormattedName(player.getUniqueId()) + "&c has been eliminated."));
                    }
                });
                LMSHandler.killTheFuckingPlayer(player);
            } else {
                EventHandler.removePlayerFromEvent(player);
            }
        }
    }

    @org.bukkit.event.EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack itemStack = event.getItemDrop().getItemStack();
        Material itemType = itemStack.getType();

        if (itemType == Material.GLASS_BOTTLE || itemType == Material.BOWL) {
            event.getItemDrop().remove();
        }
    }

    @org.bukkit.event.EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getItem().getType() != Material.MUSHROOM_SOUP || !event.getAction().name().contains("RIGHT_")) {
            return;
        }

        Event currentEvent = EventHandler.getCurrentEvent();
        Player player = event.getPlayer();

        if ((currentEvent != null && currentEvent.playerStates.get(player.getUniqueId()) == EventPlayerState.FIGHTING && currentEvent.getKit() == KitType.byId("SOUP") && player.getHealth() <= 19)) {
            double current = player.getHealth();
            double max = player.getMaxHealth();

            player.getItemInHand().setType(Material.BOWL);
            player.setHealth(Math.min(max, current + 7D));
        }
    }
}
