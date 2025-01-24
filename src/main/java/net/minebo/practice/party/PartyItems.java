package net.minebo.practice.party;

import net.minebo.practice.Practice;
import net.minebo.practice.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.experimental.UtilityClass;

import static net.minebo.practice.misc.Lang.LEFT_ARROW;
import static net.minebo.practice.misc.Lang.RIGHT_ARROW;
import static org.bukkit.ChatColor.*;

@UtilityClass
public final class PartyItems {

    public static final Material ICON_TYPE = Material.NETHER_STAR;

    public static final ItemStack LEAVE_PARTY_ITEM = new ItemStack(Material.INK_SACK, 1, DyeColor.RED.getDyeData());
    public static final ItemStack ASSIGN_CLASSES = new ItemStack(Material.ITEM_FRAME);
    public static final ItemStack START_TEAM_SPLIT_ITEM = new ItemStack(Material.DIAMOND_SWORD);
    public static final ItemStack START_FFA_ITEM = new ItemStack(Material.GOLD_SWORD);
    public static final ItemStack OTHER_PARTIES_ITEM = new ItemStack(Material.SKULL_ITEM);

    static {
        ItemUtils.setDisplayName(LEAVE_PARTY_ITEM, RED + "Leave Party");
        ItemUtils.setDisplayName(ASSIGN_CLASSES, LIGHT_PURPLE + "HCF Kits");
        ItemUtils.setDisplayName(START_TEAM_SPLIT_ITEM, GREEN + "Start Team Split");
        ItemUtils.setDisplayName(START_FFA_ITEM, GOLD + "Start Party FFA");
        ItemUtils.setDisplayName(OTHER_PARTIES_ITEM, YELLOW + "Other Parties");
    }

    public static ItemStack icon(Party party) {
        ItemStack item = new ItemStack(ICON_TYPE);

        String leaderName = Practice.getInstance().getUuidCache().name(party.getLeader());
        String displayName = ChatColor.AQUA + "Party Info";

        ItemUtils.setDisplayName(item, displayName);
        return item;
    }

}
