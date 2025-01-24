package net.minebo.practice.events.listener;

import net.minebo.practice.Practice;
import net.minebo.practice.events.EventItems;
import net.minebo.practice.events.menu.EventKitSelectMenu;
import net.minebo.practice.events.menu.EventSelectMenu;
import net.minebo.practice.queue.QueueItems;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventItemListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getItem() == null) {
            return;
        }

        if (event.getItem().isSimilar(EventItems.getEventItem())) {
            new EventSelectMenu(eventType -> {
                if (!Practice.getInstance().getEventHandler().hasPermissionToHost(event.getPlayer(), eventType)) {
                    event.getPlayer().sendMessage(ChatColor.RED + "You don't have permission to host " + eventType.getName() + " events.");
                    return;
                }
                event.getPlayer().closeInventory();
                if (eventType.isAllowKitSelection()) {
                    new EventKitSelectMenu(kitType -> {
                        event.getPlayer().closeInventory();
                        event.getPlayer().sendMessage(ChatColor.GREEN + "Trying to start event...");
                        Practice.getInstance().getEventHandler().hostEvent(event.getPlayer(), eventType, kitType);
                    }, "Select kit for event", eventType).openMenu(event.getPlayer());
                } else {
                    event.getPlayer().sendMessage(ChatColor.GREEN + "Trying to start event...");
                    Practice.getInstance().getEventHandler().hostEvent(event.getPlayer(), eventType, null);
                }
            }, "Select an event").openMenu(event.getPlayer());
        } else if (event.getItem().isSimilar(EventItems.getLeaveItem())) {
            if(net.minebo.practice.events.EventHandler.getCurrentEvent() != null) {
                if(net.minebo.practice.events.EventHandler.getCurrentEvent().isPlayerInEvent(event.getPlayer().getUniqueId())) {
                    net.minebo.practice.events.EventHandler.getCurrentEvent().removePlayer(event.getPlayer().getUniqueId());
                    event.getPlayer().sendMessage(ChatColor.RED + "You left the event!");
                }
            }
        }
    }

}
