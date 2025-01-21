package net.minebo.practice.profile.setting.menu;

import com.google.common.base.Preconditions;

import net.minebo.practice.Practice;
import net.minebo.practice.profile.setting.Setting;
import net.minebo.practice.profile.setting.SettingHandler;
import net.minebo.practice.util.menu.Button;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

/**
 * Button used by {@link SettingsMenu} to render a {@link Setting}
 */
final class SettingButton extends Button {

    private final Setting setting;

    SettingButton(Setting setting) {
        this.setting = Preconditions.checkNotNull(setting, "setting");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.YELLOW.toString() + ChatColor.BOLD + setting.getName();
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = new ArrayList<>();
        Boolean isEnabled = Practice.getInstance().getSettingHandler().getSetting(player, setting);

        description.add("");
        description.addAll(setting.getDescription());
        description.add("");

        description.add(ChatColor.GRAY + "Status: " + (isEnabled ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        description.add("");
        description.add(ChatColor.AQUA + "► Click to " + (!isEnabled ? "Enable" : "Disable"));

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return setting.getIcon();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        if (!setting.canUpdate(player)) {
            return;
        }

        SettingHandler settingHandler = Practice.getInstance().getSettingHandler();

        boolean enabled = !settingHandler.getSetting(player, setting);
        settingHandler.updateSetting(player, setting, enabled);
    }

}