package net.minebo.practice.match.listener;

import net.minebo.practice.Practice;
import net.minebo.practice.match.Match;
import net.minebo.practice.match.MatchHandler;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public final class MatchEnderPearlDamageListener implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL && event.getEntity() instanceof Player && event.getDamager() instanceof EnderPearl) {
            Player player = (Player) event.getEntity();
            MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
            Match match = matchHandler.getMatchPlaying(player);

            if (match != null && !match.getKitType().isPearlDamage()) {
                event.setCancelled(true);
            }
        }
    }

}