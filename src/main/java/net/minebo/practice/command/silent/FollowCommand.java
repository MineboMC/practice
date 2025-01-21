package net.minebo.practice.command.silent;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.minebo.practice.Practice;
import net.minebo.practice.profile.follow.FollowHandler;
import net.minebo.practice.match.MatchHandler;
import net.minebo.practice.profile.setting.Setting;
import net.minebo.practice.profile.setting.SettingHandler;
import net.minebo.practice.misc.Validation;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FollowCommand extends BaseCommand {

    @CommandAlias("setsolo")
    @Description("Follow a player.")
    @CommandPermission("potpvp.staff")
    @CommandCompletion("@players")
    public void follow(Player sender, Player target) {
        if (!Validation.canFollowSomeone(sender)) {
            return;
        }

        FollowHandler followHandler = Practice.getInstance().getFollowHandler();
        SettingHandler settingHandler = Practice.getInstance().getSettingHandler();
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        if (sender == target) {
            sender.sendMessage(ChatColor.RED + "No, you can't follow yourself.");
            return;
        } else if (!settingHandler.getSetting(target, Setting.ALLOW_SPECTATORS)) {
            if (sender.isOp()) {
                sender.sendMessage(ChatColor.RED + "Bypassing " + target.getName() + "'s no spectators preference...");
            } else {
                sender.sendMessage(ChatColor.RED + target.getName() + " doesn't allow spectators at the moment.");
                return;
            }
        }

        followHandler.getFollowing(sender).ifPresent(fo -> new UnfollowCommand().unfollow(sender));

        if (matchHandler.isSpectatingMatch(sender)) {
            matchHandler.getMatchSpectating(sender).removeSpectator(sender);
        }

        followHandler.startFollowing(sender, target);
    }
}