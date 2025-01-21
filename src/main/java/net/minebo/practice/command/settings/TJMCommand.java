package net.minebo.practice.command.settings;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import net.minebo.practice.Practice;
import net.minebo.practice.profile.setting.Setting;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 4/14/2022
 * Project: potpvp-reprised
 */

public class TJMCommand extends BaseCommand {

    @CommandAlias("tjm")
    @Description("Toggle tournament join messages.")
    public void joinMessages(Player sender) {
        boolean oldValue = Practice.getInstance().getSettingHandler().getSetting(sender, Setting.SEE_TOURNAMENT_JOIN_MESSAGE);
        if (!oldValue) {
            Practice.getInstance().getSettingHandler().updateSetting(sender, Setting.SEE_TOURNAMENT_JOIN_MESSAGE, true);
            sender.sendMessage(ChatColor.GREEN + "Enabled tournament join messages.");
            return;
        }

        Practice.getInstance().getSettingHandler().updateSetting(sender, Setting.SEE_TOURNAMENT_JOIN_MESSAGE, false);
        sender.sendMessage(ChatColor.GREEN + "Disabled tournament join messages.");
    }

}
