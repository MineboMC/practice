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
 * /toggleglobalchat command, allows players to toggle {@link Setting#ENABLE_GLOBAL_CHAT} setting
 */
public class ToggleGlobalChatCommand extends BaseCommand {

    @CommandAlias("toggleglobalchat|tgc")
    @Description("Toggle global chat.")
    public void toggleGlobalChat(Player sender) {
        if (!Setting.ENABLE_GLOBAL_CHAT.canUpdate(sender)) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return;
        }

        SettingHandler settingHandler = Practice.getInstance().getSettingHandler();
        boolean enabled = !settingHandler.getSetting(sender, Setting.ENABLE_GLOBAL_CHAT);

        settingHandler.updateSetting(sender, Setting.ENABLE_GLOBAL_CHAT, enabled);

        if (enabled) {
            sender.sendMessage(ChatColor.GREEN + "Toggled global chat on.");
        } else {
            sender.sendMessage(ChatColor.RED + "Toggled global chat off.");
        }
    }

}