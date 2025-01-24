package net.minebo.practice.match.postmatchinv.menu;

import com.google.common.collect.ImmutableList;

import net.minebo.practice.Practice;
import net.minebo.practice.kit.kittype.HealingMethod;
import net.minebo.practice.util.menu.Button;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

final class PostMatchHealsLeftButton extends Button {

    private final UUID player;
    private final HealingMethod healingMethod;
    private final int healsRemaining;
    private final int missedHeals;
    private final int usedHeals;

    PostMatchHealsLeftButton(UUID player, HealingMethod healingMethod, int healsRemaining, int missedHeals, int usedHeals) {
        this.player = player;
        this.healingMethod = healingMethod;
        this.healsRemaining = healsRemaining;
        this.missedHeals = missedHeals;
        this.usedHeals = usedHeals;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.RED.toString() + ChatColor.BOLD + "Heals";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
                ChatColor.DARK_RED + "* " + ChatColor.WHITE + "Used: " + ChatColor.RED + usedHeals,
                ChatColor.DARK_RED + "* " + ChatColor.WHITE + "Remaining: " + ChatColor.RED + healsRemaining,
                ChatColor.DARK_RED + "* " + ChatColor.WHITE + "Missed: " + ChatColor.RED + missedHeals,
                ChatColor.DARK_RED + "* " + ChatColor.WHITE + "Accuracy: " + ChatColor.RED + (getPotionAccuracy() == -1 ? "N/A" : getPotionAccuracy() + "%"));
    }

    @Override
    public Material getMaterial(Player player) {
        return healingMethod.getIconType();
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemStack item = super.getButtonItem(player);
        item.setDurability(healingMethod.getIconDurability());
        return item;
    }

    @Override
    public int getAmount(Player player) {
        return healsRemaining;
    }

    public int getPotionAccuracy() {
        if (usedHeals == 0) {
            return -1;
        }

        return (100 - (int)((missedHeals / usedHeals) * 100.0));
    }


}