package net.minebo.practice.events;

import lombok.experimental.UtilityClass;
import net.minebo.practice.Practice;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@UtilityClass
public class EventUtils {

    public static void resetInventory(Player player) {
        EventHandler EventHandler = Practice.getInstance().getEventHandler();

        if (EventHandler.getCurrentEvent() == null) {
            return;
        }

        player.getInventory().clear();

        player.getInventory().setItem(8, EventItems.getLeaveItem());

        Bukkit.getScheduler().runTaskLater(Practice.getInstance(), player::updateInventory, 1L);
    }

    public static void resetFightingInventory(Player player) {
        EventHandler EventHandler = Practice.getInstance().getEventHandler();

        if (EventHandler.getCurrentEvent() == null) {
            return;
        }

        player.getInventory().clear();
        Bukkit.getScheduler().runTaskLater(Practice.getInstance(), player::updateInventory, 1L);
    }



}
