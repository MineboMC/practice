package net.minebo.practice.events;

import lombok.experimental.UtilityClass;
import net.minebo.practice.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.ChatColor.*;

@UtilityClass
public final class EventItems {

    public ItemStack getEventItem() {
        return ItemBuilder.of(Material.EMERALD).name(GREEN + "Host an Event").build();
    }

    public ItemStack getLeaveItem(){
        return ItemBuilder.of(Material.RED_ROSE).name(RED + "Leave").build();
    }

}