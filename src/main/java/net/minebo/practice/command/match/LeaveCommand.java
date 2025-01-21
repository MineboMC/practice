package net.minebo.practice.command.match;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import net.minebo.practice.Practice;
import net.minebo.practice.match.Match;
import net.minebo.practice.match.MatchHandler;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LeaveCommand extends BaseCommand {

    @CommandAlias("leave")
    @Description("Leave a match.")
    @CommandPermission("potpvp.admin")
    public void leave(Player sender) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        if (matchHandler.isPlayingMatch(sender)) {
            sender.sendMessage(ChatColor.RED + "You cannot do this while playing in a match.");
            return;
        }

        sender.sendMessage(ChatColor.YELLOW + "Teleporting you to spawn...");

        Match spectating = matchHandler.getMatchSpectating(sender);

        if (spectating == null) {
            Practice.getInstance().getLobbyHandler().returnToLobby(sender);
        } else {
            spectating.removeSpectator(sender);
        }
    }

}