package net.minebo.practice.command.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import com.google.common.collect.ImmutableList;

import net.minebo.practice.Practice;
import net.minebo.practice.match.MatchHandler;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * Generic /help command, changes message sent based on if sender is playing in
 * or spectating a match.
 */
public class HelpCommand extends BaseCommand {

    private static final List<String> HELP_MESSAGE_HEADER = ImmutableList.of(
        "",
        "§6§lHelpful Information",
        "§fMore info @ §eminebo.net",
        ""
    );

    private static final List<String> HELP_MESSAGE_LOBBY = ImmutableList.of(
        "§6Common Commands:",
        "§f /duel <player> §8- §7Challenge a player to a duel",
        "§f /party invite <player> §8- §7Invite a player to a party",
        "",
        "§6Other Commands:",
        "§f /party help §8- §7Information on party commands",
        "§f /report <player> <reason> §8- §7Report a player for violating the rules",
        "§f /request <message> §8- §7Request assistance from a staff member"
    );

    private static final List<String> HELP_MESSAGE_MATCH = ImmutableList.of(
        "§6Common Commands:",
        "§f /spectate <player> §8- §7Spectate a player in a match",
        "§f /report <player> <reason> §8- §7Report a player for violating the rules",
        "§f /request <message> §8- §7Request assistance from a staff member"
    );

    private static final List<String> HELP_MESSAGE_FOOTER = ImmutableList.of(
        "",
        "§5Server Information:",
        "§f Teamspeak§7: §ets.minebo.net",
        "§f Rules§7: §eminebo.net/rules",
        "§f Store§7: §ebuy.minebo.net",
        ""
    );

    @CommandAlias("help|?")
    @Description("Help command.")
    public void help(Player sender) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        HELP_MESSAGE_HEADER.forEach(sender::sendMessage);

        if (matchHandler.isPlayingOrSpectatingMatch(sender)) {
            HELP_MESSAGE_MATCH.forEach(sender::sendMessage);
        } else {
            HELP_MESSAGE_LOBBY.forEach(sender::sendMessage);
        }

        HELP_MESSAGE_FOOTER.forEach(sender::sendMessage);
    }

}
