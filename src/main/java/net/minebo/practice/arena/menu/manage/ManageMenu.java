package net.minebo.practice.arena.menu.manage;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minebo.practice.arena.menu.manageschematics.ManageSchematicsMenu;
import net.minebo.practice.kit.kittype.menu.manage.ManageKitTypeMenu;
import net.minebo.practice.kit.kittype.menu.select.SelectKitTypeMenu;
import net.minebo.practice.util.menu.Button;
import net.minebo.practice.util.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;
import java.util.Map;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 4/19/2022
 * Project: potpvp-reprised
 */

public class ManageMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "Admin Management Menu";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        return ImmutableMap.of(
                3, new ManageKitButton(),
                5, new ManageArenaButton()
        );
    }

    private static class ManageKitButton extends Button {

        @Override
        public String getName(Player player) {
            return ChatColor.YELLOW + "Manage kit type definitions";
        }

        @Override
        public List<String> getDescription(Player player) {
            return ImmutableList.of();
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.DIAMOND_SWORD;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            player.closeInventory();

            new SelectKitTypeMenu((kitType) -> {
                player.closeInventory();
                new ManageKitTypeMenu(kitType).openMenu(player);
            }, false, "Manage Kit Type...").openMenu(player);
        }

    }

    private static class ManageArenaButton extends Button {

        @Override
        public String getName(Player player) {
            return ChatColor.YELLOW + "Manage the arena grid";
        }

        @Override
        public List<String> getDescription(Player player) {
            return ImmutableList.of();
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.IRON_PICKAXE;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            player.closeInventory();
            new ManageSchematicsMenu().openMenu(player);
        }

    }
}
