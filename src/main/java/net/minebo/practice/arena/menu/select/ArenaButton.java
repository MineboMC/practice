package net.minebo.practice.arena.menu.select;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.google.common.collect.Lists;

import lombok.AllArgsConstructor;
import net.minebo.practice.util.menu.Button;
import net.minebo.practice.util.Callback;

@AllArgsConstructor
public class ArenaButton extends Button {

    private String mapName;
    private boolean isSelected;
    private Callback<String> selectionCallback; // Updates the selected map

    @Override
    public String getName(Player player) {
        return (isSelected ? ChatColor.GREEN : ChatColor.RED) + mapName;
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> lines = Lists.newLinkedList();

        if (isSelected) {
            lines.add(ChatColor.GRAY + "Click to deselect this arena.");
        } else {
            lines.add(ChatColor.GRAY + "Click to select this arena.");
        }

        return lines;
    }

    @Override
    public Material getMaterial(Player player) {
        return isSelected ? Material.MAP : Material.EMPTY_MAP;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        if (isSelected) {
            player.sendMessage(ChatColor.RED + "Deselected " + mapName + ".");
            selectionCallback.callback(null); // Deselect the arena
        } else {
            player.sendMessage(ChatColor.GREEN + "Selected " + mapName + ".");
            selectionCallback.callback(mapName); // Select the arena
        }
    }
}
