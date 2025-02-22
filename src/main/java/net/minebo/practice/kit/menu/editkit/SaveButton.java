package net.minebo.practice.kit.menu.editkit;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import net.minebo.practice.Practice;
import net.minebo.practice.kit.Kit;
import net.minebo.practice.kit.menu.kits.KitsMenu;
import net.minebo.practice.util.InventoryUtils;
import net.minebo.practice.util.ItemUtils;
import net.minebo.practice.util.menu.Button;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

final class SaveButton extends Button {

    private final Kit kit;

    SaveButton(Kit kit) {
        this.kit = Preconditions.checkNotNull(kit, "kit");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN + "Save";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            "",
            ChatColor.YELLOW + "Click this to save your kit."
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.INK_SACK;
    }

    @Override
    public byte getDamageValue(Player player) {
        return DyeColor.LIME.getDyeData();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        kit.setInventoryContents(player.getInventory().getContents());
        Practice.getInstance().getKitHandler().saveKitsAsync(player);

        player.setItemOnCursor(new ItemStack(Material.AIR));

        player.closeInventory();
        InventoryUtils.resetInventoryDelayed(player);

        new KitsMenu(kit.getType()).openMenu(player);

        ItemStack[] defaultInventory = kit.getType().getDefaultInventory();
        int foodInDefault = ItemUtils.countStacksMatching(defaultInventory, ItemUtils.EDIBLE_PREDICATE);
        int pearlsInDefault = ItemUtils.countStacksMatching(defaultInventory, v -> v.getType() == Material.ENDER_PEARL);

        if (foodInDefault > 0 && kit.countFood() == 0) {
            player.sendMessage(ChatColor.RED + "Your saved kit is missing food.");
        }

        if (pearlsInDefault > 0 && kit.countPearls() == 0) {
            player.sendMessage(ChatColor.RED + "Your saved kit is missing enderpearls.");
        }
    }

}