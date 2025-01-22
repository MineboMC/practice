package net.minebo.practice.events;

import net.minebo.practice.Practice;
import net.minebo.practice.events.enums.EventPlayerState;
import net.minebo.practice.events.enums.EventType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventListener implements Listener {
    EventHandler EventHandler = Practice.getInstance().getEventHandler();

    @org.bukkit.event.EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (EventHandler.getCurrentEvent() != null && (EventHandler.getCurrentEvent().isPlayerInEvent(player.getUniqueId()) && EventHandler.getCurrentEvent() != null) && EventHandler.getCurrentEvent().playerStates.get(player.getUniqueId()) != EventPlayerState.FIGHTING) {
            event.setCancelled(true);
        }
    }

    @org.bukkit.event.EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (EventHandler.getCurrentEvent() != null && (EventHandler.getCurrentEvent().isPlayerInEvent(player.getUniqueId()) && EventHandler.getCurrentEvent() != null) && EventHandler.getCurrentEvent().playerStates.get(player.getUniqueId()) != EventPlayerState.FIGHTING) {
            event.setCancelled(true);
        }
    }

    @org.bukkit.event.EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (EventHandler.getCurrentEvent() != null && (EventHandler.getCurrentEvent().isPlayerInEvent(player.getUniqueId()) && EventHandler.getCurrentEvent() != null) && EventHandler.getCurrentEvent().playerStates.get(player.getUniqueId()) != EventPlayerState.FIGHTING) {
            event.setCancelled(true);
        }
    }

    @org.bukkit.event.EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (player.getItemInHand() == null || player.getItemInHand().getItemMeta() == null || player.getItemInHand().getItemMeta().getDisplayName() == null) return;
        if (player.getItemInHand().isSimilar(EventItems.getLeaveItem())) {
            EventHandler.removePlayerFromEvent(player);
        }
    }

    @org.bukkit.event.EventHandler
    public void onFallDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && EventHandler.getCurrentEvent() != null && EventHandler.getCurrentEvent().getSpectatorsAndPlayers().contains(e.getEntity().getUniqueId())) {
            if (EventHandler.getCurrentEvent().spectators.contains(e.getEntity().getUniqueId()) || EventHandler.getCurrentEvent().playerStates.get(e.getEntity().getUniqueId()) == EventPlayerState.WAITING) {
                e.setCancelled(true);
            }
        }

    }

    @org.bukkit.event.EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        Event gameEvent = EventHandler.getCurrentEvent();
        if (gameEvent == null) return;
        if (event.getEntityType() != EntityType.PLAYER) return;
        if (!gameEvent.getSpectatorsAndPlayers().contains(event.getEntity().getUniqueId())) return;


        if (gameEvent.type == EventType.SUMO || gameEvent.type == EventType.OITC) {
            event.setFoodLevel(20);
            return;
        }

        if (gameEvent.playerStates.get(event.getEntity().getUniqueId()) != EventPlayerState.FIGHTING) {
            event.setFoodLevel(20);
            return;
        }

        if (gameEvent.type == EventType.LMS && gameEvent.playerStates.get(event.getEntity().getUniqueId()) == EventPlayerState.FIGHTING) {
            event.setFoodLevel(20);
            return;
        }

        if (gameEvent.type == EventType.DEATHRACE && gameEvent.playerStates.get(event.getEntity().getUniqueId()) == EventPlayerState.FIGHTING) {
            event.setFoodLevel(20);
            return;
        }
    }

    @org.bukkit.event.EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if(EventHandler.getCurrentEvent() != null) {
            if (EventHandler.getCurrentEvent().isPlayerInEvent(event.getPlayer().getUniqueId())) {
                if (EventHandler.getCurrentEvent().playerStates.get(event.getPlayer().getUniqueId()) == EventPlayerState.WAITING || EventHandler.getCurrentEvent().playerStates.get(event.getPlayer().getUniqueId()) == EventPlayerState.DEAD || EventHandler.getCurrentEvent().playerStates.get(event.getPlayer().getUniqueId()) == EventPlayerState.SPECTATING) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
