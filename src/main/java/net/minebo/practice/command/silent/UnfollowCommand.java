package net.minebo.practice.command.silent;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.minebo.practice.Practice;
import net.minebo.practice.profile.follow.FollowHandler;
import net.minebo.practice.match.Match;
import net.minebo.practice.match.MatchHandler;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("unfollow")
public class UnfollowCommand extends BaseCommand {

    @Default
    @Description("Stop following a person.")
    @CommandPermission("potpvp.staff")
    public void unfollow(Player sender) {
        FollowHandler followHandler = Practice.getInstance().getFollowHandler();
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        if (!followHandler.getFollowing(sender).isPresent()) {
            sender.sendMessage(ChatColor.RED + "You're not following anybody.");
            return;
        }

        Match spectating = matchHandler.getMatchSpectating(sender);

        if (spectating != null) {
            spectating.removeSpectator(sender);
        }

        followHandler.stopFollowing(sender);
    }
}