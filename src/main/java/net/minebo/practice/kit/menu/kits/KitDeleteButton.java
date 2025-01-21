package net.minebo.practice.kit.menu.kits;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.minebo.practice.Practice;
import net.minebo.practice.kit.KitHandler;
import net.minebo.practice.kit.kittype.KitType;
import net.minebo.practice.util.InventoryUtils;
import net.minebo.practice.util.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public final class KitDeleteButton extends Button {

    private final KitType kitType;
    private final int slot;

    public KitDeleteButton(KitType kitType, int slot) {
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
        this.slot = slot;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.RED.toString() + ChatColor.BOLD + "Delete";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            "",
            ChatColor.RED + "Click here to delete this kit",
            ChatColor.RED + "You will " + ChatColor.BOLD + "NOT" + ChatColor.RED + " be able to",
            ChatColor.RED + "recover this kit."
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.REDSTONE_BLOCK;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.closeInventory();
        InventoryUtils.resetInventoryDelayed(player);

        KitHandler kitHandler = Practice.getInstance().getKitHandler();
        kitHandler.removeKit(player, kitType, this.slot);

        new KitsMenu(kitType).openMenu(player);
    }

}