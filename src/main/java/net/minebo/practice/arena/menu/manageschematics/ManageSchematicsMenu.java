package net.minebo.practice.arena.menu.manageschematics;

import net.minebo.practice.Practice;
import net.minebo.practice.arena.ArenaHandler;
import net.minebo.practice.arena.ArenaSchematic;
import net.minebo.practice.arena.menu.manage.ManageMenu;
import net.minebo.practice.util.menu.Button;
import net.minebo.practice.util.menu.Menu;

import net.minebo.practice.util.menu.buttons.BackButton;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class ManageSchematicsMenu extends Menu {

    public ManageSchematicsMenu() {
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return "Manage schematics";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        ArenaHandler arenaHandler = Practice.getInstance().getArenaHandler();
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        buttons.put(index++, new BackButton(new ManageMenu()));

        for (ArenaSchematic schematic : arenaHandler.getSchematics()) {
            buttons.put(index++, new ManageSchematicButton(schematic));
        }

        return buttons;
    }

}