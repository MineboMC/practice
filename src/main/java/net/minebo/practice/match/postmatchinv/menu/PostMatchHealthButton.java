package net.minebo.practice.match.postmatchinv.menu;

import com.google.common.collect.ImmutableList;

import net.minebo.practice.util.menu.Button;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

final class PostMatchHealthButton extends Button {

    private final double health;

    PostMatchHealthButton(double health) {
        this.health = health;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.RED + ChatColor.BOLD.toString() + "Health";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(ChatColor.DARK_RED + "* " + ChatColor.WHITE + health + ChatColor.RED + " ❤");
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.SPECKLED_MELON;
    }

    @Override
    public int getAmount(Player player) {
        return (int) Math.ceil(health);
    }

}