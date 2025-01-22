package net.minebo.practice.lobby.menu;

import java.util.HashMap;
import java.util.Map;

import net.minebo.practice.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.minebo.practice.kit.kittype.KitType;
import net.minebo.practice.lobby.menu.statistics.GlobalEloButton;
import net.minebo.practice.lobby.menu.statistics.KitButton;
import net.minebo.practice.lobby.menu.statistics.PlayerButton;
import net.minebo.practice.util.menu.Button;
import net.minebo.practice.util.menu.Menu;
import org.jetbrains.annotations.NotNull;

public final class StatisticsMenu extends Menu {

    private static final Button BLACK_PANE = Button.fromItem(ItemBuilder.of(Material.STAINED_GLASS_PANE).data(DyeColor.BLACK.getData()).name("§r").build());

    public StatisticsMenu() {
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.YELLOW.toString() + ChatColor.BOLD + "Statistics";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(getSlot(3, 1), new PlayerButton());
        buttons.put(getSlot(5, 1), new GlobalEloButton());

        int y = 3;
        int x = 1;

        for (KitType kitType : KitType.getAllTypes()) {
            if (!kitType.isSupportsRanked()) continue;

            buttons.put(getSlot(x++, y), new KitButton(kitType));

            if (x == 8) {
                y++;
                x = 1;
            }
        }

        for (int i = 0; i < 54; i++) {
            buttons.putIfAbsent(i, BLACK_PANE);
        }

        return buttons;
    }

    @Override
    public int size(Player player) {
        return 9 * 6;
    }

}