package net.minebo.practice.kit.menu.editkit;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import net.minebo.practice.kit.menu.kits.KitsMenu;
import net.minebo.practice.kit.kittype.KitType;
import net.minebo.practice.util.InventoryUtils;
import net.minebo.practice.util.menu.Button;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

final class CancelKitEditButton extends Button {

    private final KitType kitType;

    CancelKitEditButton(KitType kitType) {
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.RED + "Cancel";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            "",
            ChatColor.YELLOW + "Click this to abort editing your kit,",
            ChatColor.YELLOW + "and return to the kit menu."
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.REDSTONE;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.closeInventory();
        InventoryUtils.resetInventoryDelayed(player);

        new KitsMenu(kitType).openMenu(player);
    }

}