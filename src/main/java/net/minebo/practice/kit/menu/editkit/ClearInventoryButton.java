package net.minebo.practice.kit.menu.editkit;

import com.google.common.collect.ImmutableList;

import net.minebo.practice.Practice;
import net.minebo.practice.util.menu.Button;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

final class ClearInventoryButton extends Button {

    @Override
    public String getName(Player player) {
        return ChatColor.YELLOW + "Clear Inventory";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            "",
            ChatColor.YELLOW + "This will clear your inventory",
            ChatColor.YELLOW + "so you can start over."
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.INK_SACK;
    }

    @Override
    public byte getDamageValue(Player player) {
        return DyeColor.RED.getDyeData();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.getInventory().clear();

        Bukkit.getScheduler().runTaskLater(Practice.getInstance(), player::updateInventory, 1L);
    }

}