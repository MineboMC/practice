package net.minebo.practice.command.duel;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.google.common.collect.ImmutableList;

import net.minebo.practice.misc.Lang;
import net.minebo.practice.Practice;
import net.minebo.practice.match.duel.DuelHandler;
import net.minebo.practice.match.duel.DuelInvite;
import net.minebo.practice.match.duel.PartyDuelInvite;
import net.minebo.practice.match.duel.PlayerDuelInvite;
import net.minebo.practice.match.Match;
import net.minebo.practice.match.MatchHandler;
import net.minebo.practice.match.MatchTeam;
import net.minebo.practice.party.Party;
import net.minebo.practice.party.PartyHandler;
import net.minebo.practice.misc.Validation;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AcceptCommand extends BaseCommand {

    @CommandAlias("accept")
    @Description("Accept a duel.")
    @CommandCompletion("@players")
    public void accept(CommandSender sender, OnlinePlayer target) {

        if (target == null) {
            return;
        }

        if (sender == target) {
            sender.sendMessage(ChatColor.RED + "You can't accept a duel from yourself!");
            return;
        }

        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        DuelHandler duelHandler = Practice.getInstance().getDuelHandler();

        Party senderParty = partyHandler.getParty((Player) sender);
        Party targetParty = partyHandler.getParty(target.getPlayer());

        if (senderParty != null && targetParty != null) {
            // party accepting from party (legal)
            PartyDuelInvite invite = duelHandler.findInvite(targetParty, senderParty);

            if (invite != null) {
                acceptParty(((Player) sender), senderParty, targetParty, invite);
            } else {
                // we grab the leader's name as the member targeted might not be the leader
                String leaderName = Practice.getInstance().getUuidCache().name(targetParty.getLeader());
                sender.sendMessage(ChatColor.RED + "Your party doesn't have a duel invite from " + leaderName + "'s party.");
            }
        } else if (senderParty == null && targetParty == null) {
            // player accepting from player (legal)
            PlayerDuelInvite invite = duelHandler.findInvite(target.getPlayer(), ((Player) sender));

            if (invite != null) {
                acceptPlayer(((Player) sender), target.getPlayer(), invite);
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have a duel invite from " + target.getPlayer().getName() + ".");
            }
        } else if (senderParty == null) {
            // player accepting from party (illegal)
            sender.sendMessage(ChatColor.RED + "You don't have a duel invite from " + target.getPlayer().getName() + ".");
        } else {
            // party accepting from player (illegal)
            sender.sendMessage(ChatColor.RED + "Your party doesn't have a duel invite from " + target.getPlayer().getName() + "'s party.");
        }
    }

    private void acceptParty(Player sender, Party senderParty, Party targetParty, DuelInvite<?> invite) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        DuelHandler duelHandler = Practice.getInstance().getDuelHandler();

        if (!senderParty.isLeader(sender.getUniqueId())) {
            sender.sendMessage(Lang.NOT_LEADER_OF_PARTY);
            return;
        }

        if (!Validation.canAcceptDuel(senderParty, targetParty, sender)) {
            return;
        }

        Match match = matchHandler.startMatch(
                ImmutableList.of(new MatchTeam(senderParty.getMembers()), new MatchTeam(targetParty.getMembers())),
                invite.getKitType(),
                false,
                true, // see Match#allowRematches,
                invite.getArenaName()
        );

        if (match != null) {
            // only remove invite if successful
            duelHandler.removeInvite(invite);
        } else {
            senderParty.message(Lang.ERROR_WHILE_STARTING_MATCH);
            targetParty.message(Lang.ERROR_WHILE_STARTING_MATCH);
        }
    }

    private void acceptPlayer(Player sender, Player target, DuelInvite<?> invite) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        DuelHandler duelHandler = Practice.getInstance().getDuelHandler();

        if (!Validation.canAcceptDuel(sender, target)) {
            return;
        }

        Match match = matchHandler.startMatch(
                ImmutableList.of(new MatchTeam(sender.getUniqueId()), new MatchTeam(target.getUniqueId())),
                invite.getKitType(),
                false,
                true, // see Match#allowRematches,
                invite.getArenaName()
        );

        if (match != null) {
            // only remove invite if successful
            duelHandler.removeInvite(invite);
        } else {
            sender.sendMessage(Lang.ERROR_WHILE_STARTING_MATCH);
            target.sendMessage(Lang.ERROR_WHILE_STARTING_MATCH);
        }
    }

}