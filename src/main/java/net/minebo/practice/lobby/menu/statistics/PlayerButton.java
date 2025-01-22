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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerButton extends Button {

    private static EloHandler eloHandler = Practice.getInstance().getEloHandler();

    @Override
    public String getName(Player player) {
        return player.getDisplayName() + ChatColor.GRAY + " | "  + ChatColor.WHITE + "Statistics";
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = Lists.newArrayList();

        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");

        for (KitType kitType : KitType.getAllTypes()) {
            if (kitType.isSupportsRanked()) {
                description.add(kitType.getColoredDisplayName() + ChatColor.GRAY + ": " + ChatColor.WHITE + eloHandler.getElo(player, kitType));
            }
        }

        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");
        description.add(ChatColor.GOLD + "Global" + ChatColor.GRAY + ": " + ChatColor.WHITE + eloHandler.getGlobalElo(player.getUniqueId()));
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

    @Override
    public ItemStack getButtonItem(Player player) {
        // Create the item with the specified material, amount, and damage value
        ItemStack buttonItem = new ItemStack(this.getMaterial(player), this.getAmount(player), (short) this.getDamageValue(player));
        ItemMeta meta = buttonItem.getItemMeta();

        // Set the display name
        meta.setDisplayName(this.getName(player));

        // Set the lore if available
        List<String> description = this.getDescription(player);
        if (description != null) {
            meta.setLore(description);
        }

        // Check if the item is a player head (skull)
        if (buttonItem.getType() == Material.SKULL_ITEM && buttonItem.getDurability() == 3) {
            if (meta instanceof SkullMeta) {
                SkullMeta skullMeta = (SkullMeta) meta;
                skullMeta.setOwner(player.getName()); // Set the skull owner
                buttonItem.setItemMeta(skullMeta);
            }
        } else {
            // Set the generic item meta for non-skull items
            buttonItem.setItemMeta(meta);
        }

        return buttonItem;
    }

}
