package net.minebo.practice.kit.menu.kits;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import net.minebo.practice.Practice;
import net.minebo.practice.kit.Kit;
import net.minebo.practice.kit.KitHandler;
import net.minebo.practice.kit.menu.editkit.EditKitMenu;
import net.minebo.practice.kit.kittype.KitType;
import net.minebo.practice.util.menu.Button;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;
import java.util.Optional;

final class KitEditButton extends Button {

    private final Optional<Kit> kitOpt;
    private final KitType kitType;
    private final int slot;

    KitEditButton(Optional<Kit> kitOpt, KitType kitType, int slot) {
        this.kitOpt = Preconditions.checkNotNull(kitOpt, "kitOpt");
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
        this.slot = slot;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN.toString() + ChatColor.BOLD + "Edit " + kitType.getDisplayName() + " #" + slot;
    }

    @Override
    public List<String> getDescription(Player player) {
        return kitOpt.map(kit -> ImmutableList.of(
                "",
                ChatColor.YELLOW + "Name: " + ChatColor.WHITE + kit.getName(),
                ChatColor.GREEN + "Heals: " + ChatColor.WHITE + kit.countHeals(),
                ChatColor.RED + "Debuffs: " + ChatColor.WHITE + kit.countDebuffs()
        )).orElse(ImmutableList.of());
    }

    @Override
    public Material getMaterial(Player player) {
        return kitOpt.isPresent() ? Material.DIAMOND_SWORD : Material.STONE_SWORD;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        Kit resolvedKit = kitOpt.orElseGet(() -> {
            KitHandler kitHandler = Practice.getInstance().getKitHandler();
            return kitHandler.saveDefaultKit(player, kitType, this.slot);
        });

        new EditKitMenu(resolvedKit).openMenu(player);
    }

}