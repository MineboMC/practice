package net.minebo.practice.events;

import lombok.experimental.UtilityClass;
import net.minebo.practice.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.ChatColor.*;

@UtilityClass
public final class EventItems {

    public ItemStack getEventItem() {
        return ItemBuilder.of(Material.EMERALD).name(GRAY.toString() + "» " + YELLOW + BOLD.toString() + "Events" + GRAY.toString() + " «").build();
    }

    public ItemStack getLeaveItem(){
        return ItemBuilder.of(Material.RED_ROSE).name(GRAY.toString() + "» " + RED + BOLD.toString() + "Leave" + GRAY.toString() + " «").build();
    }

}