package net.minebo.practice.command.silent;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import net.minebo.practice.Practice;
import net.minebo.practice.command.match.LeaveCommand;

import org.bukkit.entity.Player;

public class SilentFollowCommand extends BaseCommand {

    @CommandAlias("silentfollow|sfollow")
    @Description("Silently follow a person.")
    @CommandPermission("potpvp.staff")
    @CommandCompletion("@players")
    public void silentfollow(Player sender, OnlinePlayer target) {

        if (target == null) {
            return;
        }

        new SilentCommand().silent(sender);

        if (Practice.getInstance().getPartyHandler().hasParty(sender)) {
            new LeaveCommand().leave(sender);
        }

        new FollowCommand().follow(sender, target);
    }

}
