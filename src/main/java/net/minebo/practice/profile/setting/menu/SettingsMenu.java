package net.minebo.practice.profile.setting.menu;

import net.minebo.practice.lobby.menu.statistics.KitButton;
import net.minebo.practice.profile.setting.Setting;
import net.minebo.practice.util.menu.Button;
import net.minebo.practice.util.menu.Menu;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Menu used by /settings to let players toggle settings
 */
public final class SettingsMenu extends Menu {

    public SettingsMenu() {
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return "Edit settings";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int x = 1;
        int y = 0;

        for (Setting setting : Setting.values()) {
            if (setting.canUpdate(player)) {
                buttons.put(getSlot(x++, y), new SettingButton(setting));

                if (x == 8) {
                    y++;
                    x = 1;
                }
            }
        }

        return buttons;
    }

}