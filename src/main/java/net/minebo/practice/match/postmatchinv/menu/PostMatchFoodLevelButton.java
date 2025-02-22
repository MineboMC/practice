package net.minebo.practice.match.postmatchinv.menu;

import com.google.common.collect.ImmutableList;

import net.minebo.practice.util.menu.Button;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

final class PostMatchFoodLevelButton extends Button {

    private final int foodLevel;

    PostMatchFoodLevelButton(int foodLevel) {
        this.foodLevel = foodLevel;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.YELLOW + ChatColor.BOLD.toString() + "Food";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(ChatColor.GOLD + "* " + ChatColor.WHITE + foodLevel + ChatColor.YELLOW + " Hunger");
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.COOKED_BEEF;
    }

    @Override
    public int getAmount(Player player) {
        return foodLevel;
    }

}