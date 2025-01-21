package net.minebo.practice.command.settings;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import net.minebo.practice.profile.setting.menu.SettingsMenu;

import org.bukkit.entity.Player;

/**
 * /settings, accessible by all users, opens a {@link SettingsMenu}
 */
public class SettingsCommand extends BaseCommand {

    @CommandAlias("settings|config|prefs")
    @Description("Open the settings menu.")
    public void settings(Player sender) {
        new SettingsMenu().openMenu(sender);
    }

}