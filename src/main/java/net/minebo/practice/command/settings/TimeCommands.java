package net.minebo.practice.command.settings;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import net.minebo.practice.Practice;
import net.minebo.practice.profile.setting.Setting;
import net.minebo.practice.profile.setting.SettingHandler;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * /night command, allows players to toggle {@link Setting#NIGHT_MODE} setting
 */
public class TimeCommands extends BaseCommand {

    @CommandAlias("night")
    @Description("Set your time to night.")
    public void night(Player sender) {
        SettingHandler settingHandler = Practice.getInstance().getSettingHandler();

        settingHandler.updateSetting(sender, Setting.NIGHT_MODE, true);
        sender.sendMessage(ChatColor.GREEN + "Turned night mode on.");
    }

    @CommandAlias("day")
    @Description("Set your time to day.")
    public void day(Player sender) {
        SettingHandler settingHandler = Practice.getInstance().getSettingHandler();

        settingHandler.updateSetting(sender, Setting.NIGHT_MODE, false);
        sender.sendMessage(ChatColor.GREEN + "Turned night mode off.");
    }

}