package net.minebo.practice.lobby.listener;

import net.minebo.practice.Practice;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class LobbyPreventionListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        // Check if the entity is a player
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) event.getEntity();

        // Check if the player is in the lobby
        if (Practice.getInstance().getLobbyHandler().isInLobby(player)) {
            // Cancel the event
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if(Practice.getInstance().getLobbyHandler().isInLobby(event.getPlayer())) {
            if(event.getTo().getBlockY() < 10){
                Practice.getInstance().getLobbyHandler().returnToLobby(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity target = event.getEntity();
        if (target instanceof Player) {
            if (Practice.getInstance().getLobbyHandler().isInLobby((Player) target)) {
                // Cancel damage caused by another entity
                event.setCancelled(true);
            }
        }
    }

}
