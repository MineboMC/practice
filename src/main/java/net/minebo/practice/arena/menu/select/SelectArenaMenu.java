package net.minebo.practice.arena.menu.select;

import java.util.Map;

import net.minebo.practice.match.MatchHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

import net.minebo.practice.Practice;
import net.minebo.practice.arena.ArenaSchematic;
import net.minebo.practice.kit.kittype.KitType;
import net.minebo.practice.kit.kittype.menu.select.SendDuelButton;
import net.minebo.practice.util.menu.Button;
import net.minebo.practice.util.menu.Menu;
import net.minebo.practice.util.Callback;

public class SelectArenaMenu extends Menu {

    private KitType kitType;
    private Callback<String> mapCallback; // Updated to return a single map
    private String title;
    private String selectedMap;

    public SelectArenaMenu(KitType kitType, Callback<String> mapCallback) {
        this.kitType = kitType;
        this.mapCallback = mapCallback;

        // Get all usable schematics and pick a default (random if available)
        this.selectedMap = Practice.getInstance().getArenaHandler().getSchematics().stream()
                .filter(schematic -> MatchHandler.canUseSchematic(this.kitType, schematic))
                .map(ArenaSchematic::getName)
                .findFirst() // Default to the first available map
                .orElse(null);
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.YELLOW.toString() + ChatColor.BOLD + "Select an Arena...";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        int i = 0;
        for (ArenaSchematic schematic : Practice.getInstance().getArenaHandler().getSchematics()) {
            if (MatchHandler.canUseSchematic(this.kitType, schematic)) {
                String mapName = schematic.getName();
                buttons.put(i++, new ArenaButton(mapName, mapName.equals(selectedMap), map -> {
                    selectedMap = map; // Update the selected map
                }));
            }
        }

        int bottomRight = 8;
        while (buttons.get(bottomRight) != null) {
            bottomRight += 9;
        }

        bottomRight += 9;

        buttons.put(bottomRight - 8, new SendDuelButton(selectedMap, mapCallback)); // Send only the selected map

        return buttons;
    }
}
