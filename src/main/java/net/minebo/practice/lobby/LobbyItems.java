package net.minebo.practice.lobby;

import net.minebo.practice.util.ItemUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.experimental.UtilityClass;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

import static org.bukkit.ChatColor.*;

@UtilityClass
public final class LobbyItems {

    public static final ItemStack SPECTATE_RANDOM_ITEM = new ItemStack(Material.COMPASS);
    public static final ItemStack SPECTATE_MENU_ITEM = new ItemStack(Material.PAPER);
    public static final ItemStack ENABLE_SPEC_MODE_ITEM = new ItemStack(Material.REDSTONE_TORCH_ON);
    public static final ItemStack DISABLE_SPEC_MODE_ITEM = new ItemStack(Material.LEVER);
    public static final ItemStack MANAGE_ITEM = new ItemStack(Material.ANVIL);
    public static final ItemStack UNFOLLOW_ITEM = new ItemStack(Material.INK_SACK, 1, DyeColor.RED.getDyeData());
    public static final ItemStack PLAYER_STATISTICS = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);

    static {
        ItemUtils.setDisplayName(SPECTATE_RANDOM_ITEM,  RED + "Spectate Random Match");
        ItemUtils.setDisplayName(SPECTATE_MENU_ITEM,  RED + "Spectate Menu");
        ItemUtils.setDisplayName(DISABLE_SPEC_MODE_ITEM, RED + "Leave Spectator Mode");
        ItemUtils.setDisplayName(ENABLE_SPEC_MODE_ITEM, RED + "Enable Spectator Mode");
        ItemUtils.setDisplayName(MANAGE_ITEM, RED + "Manage PotPvP");
        ItemUtils.setDisplayName(UNFOLLOW_ITEM, RED + "Stop Following");
        ItemUtils.setDisplayName(PLAYER_STATISTICS, RED + "Statistics");
    }

    public ItemStack getPlayerStatistics(Player player) {
        // Create the item with the specified material, amount, and damage value
        ItemStack buttonItem = PLAYER_STATISTICS.clone();
        ItemMeta meta = buttonItem.getItemMeta();

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