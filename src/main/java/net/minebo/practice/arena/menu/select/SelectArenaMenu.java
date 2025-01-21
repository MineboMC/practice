package net.minebo.practice.arena.menu.select;

import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minebo.practice.Practice;
import net.minebo.practice.arena.ArenaSchematic;
import net.minebo.practice.kit.kittype.KitType;
import net.minebo.practice.kit.kittype.menu.select.SendDuelButton;
import net.minebo.practice.kit.kittype.menu.select.ToggleAllButton;
import net.minebo.practice.match.MatchHandler;
import net.minebo.practice.util.menu.Button;
import net.minebo.practice.util.menu.Menu;
import net.minebo.practice.util.Callback;

public class SelectArenaMenu extends Menu {
    
    private KitType kitType;
    private Callback<Set<String>> mapsCallback;
    private String title;
    Set<String> allMaps;
    Set<String> enabledSchematics = Sets.newHashSet();
    
    public SelectArenaMenu(KitType kitType, Callback<Set<String>> mapsCallback, String title) {
        this.kitType = kitType;
        this.mapsCallback = mapsCallback;
        this.title = title;
        
        for (ArenaSchematic schematic : Practice.getInstance().getArenaHandler().getSchematics()) {
            if (MatchHandler.canUseSchematic(this.kitType, schematic)) {
                enabledSchematics.add(schematic.getName());
            }
        }
        
        this.allMaps = ImmutableSet.copyOf(enabledSchematics);
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE.toString() + ChatColor.BOLD + title;
    }

    @Override
    public Map<Integer, Button> getButtons(Player arg0) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        int i = 0;
        for (String mapName : allMaps) {
            buttons.put(i++, new ArenaButton(mapName, enabledSchematics));
        }
        
        int bottomRight = 8;
        while (buttons.get(bottomRight) != null) {
            bottomRight += 9;
        }
        
        bottomRight += 9;
        
        buttons.put(bottomRight, new ToggleAllButton(allMaps, enabledSchematics));
        buttons.put(bottomRight - 8, new SendDuelButton(enabledSchematics, mapsCallback));
        
        return buttons;
    }
    
}
