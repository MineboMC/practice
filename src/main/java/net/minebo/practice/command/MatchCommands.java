package net.minebo.practice.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.minebo.practice.Practice;
import net.minebo.practice.match.Match;
import net.minebo.practice.match.MatchHandler;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("match")
public class MatchCommands extends BaseCommand {

    @Subcommand("list")
    @Description("List all ongoing matches.")
    public void matchList(Player sender) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        for (Match match : matchHandler.getHostedMatches()) {
            sender.sendMessage(ChatColor.RED + match.getSimpleDescription(true));
        }
    }

    @Subcommand("delete")
    @Description("Check someone's match status.")
    @CommandCompletion("@players")
    public void matchStatus(CommandSender sender, Player target) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlayingOrSpectating(target);

        if (match == null) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is not playing in or spectating a match.");
            return;
        }

        for (String line : Practice.getGson().toJson(match).split("\n")) {
            sender.sendMessage("  " + ChatColor.GRAY + line);
        }
    }
}