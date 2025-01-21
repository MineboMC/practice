package net.minebo.practice.lobby.menu.statistics;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import net.minebo.practice.Practice;
import net.minebo.practice.profile.elo.EloHandler;
import net.minebo.practice.kit.kittype.KitType;
import net.minebo.practice.util.menu.Button;

public class PlayerButton extends Button {

    private static EloHandler eloHandler = Practice.getInstance().getEloHandler();

    @Override
    public String getName(Player player) {
        return getColoredName(player) + ChatColor.WHITE + ChatColor.BOLD + " | "  + ChatColor.WHITE + "Statistics";
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = Lists.newArrayList();

        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");

        for (KitType kitType : KitType.getAllTypes()) {
            if (kitType.isSupportsRanked()) {
                description.add(ChatColor.RED + kitType.getDisplayName() + ChatColor.GRAY + ": " + eloHandler.getElo(player, kitType));
            }
        }

        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");
        description.add(ChatColor.RED + "Global" + ChatColor.GRAY + ": " + eloHandler.getGlobalElo(player.getUniqueId()));
        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.SKULL_ITEM;
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte) 3;
    }

    private String getColoredName(Player player) {
        return player.getName();
    }
}
