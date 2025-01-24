package net.minebo.practice.party;

import com.google.common.collect.ImmutableList;

import net.minebo.practice.Practice;
import net.minebo.practice.kit.kittype.KitType;
import net.minebo.practice.kit.kittype.menu.select.SelectKitTypeMenu;
import net.minebo.practice.match.Match;
import net.minebo.practice.match.MatchTeam;
import net.minebo.practice.party.menu.oddmanout.OddManOutMenu;
import net.minebo.practice.misc.Validation;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class PartyUtils {

    public static void startTeamSplit(Party party, Player initiator) {
        // will be called again but we fail fast if possible
        if (!Validation.canStartTeamSplit(party, initiator)) {
            return;
        }

        new SelectKitTypeMenu(kitType -> {
            initiator.closeInventory();

            if (party.getMembers().size() % 2 == 0) {
                startTeamSplit(party, initiator, kitType, false);
            } else {
                new OddManOutMenu(oddManOut -> {
                    initiator.closeInventory();
                    startTeamSplit(party, initiator, kitType, oddManOut);
                }).openMenu(initiator);
            }
        }, "Start a Team Split...").openMenu(initiator);
    }

    public static void startTeamSplit(Party party, Player initiator, KitType kitType, boolean oddManOut) {
        if (!Validation.canStartTeamSplit(party, initiator)) {
            return;
        }

        List<UUID> members = new ArrayList<>(party.getMembers());
        Collections.shuffle(members);

        Set<UUID> team1 = new HashSet<>();
        Set<UUID> team2 = new HashSet<>();
        Player spectator = null; // only can be one

        while (members.size() >= 2) {
            team1.add(members.remove(0));
            team2.add(members.remove(0));
        }

        if (!members.isEmpty()) {
            if (oddManOut) {
                spectator = Bukkit.getPlayer(members.remove(0));
                party.message(ChatColor.YELLOW + spectator.getName() + " was selected as the odd-man out.");
            } else {
                team1.add(members.remove(0));
            }
        }

        Match match = Practice.getInstance().getMatchHandler().startMatch(
            ImmutableList.of(
                new MatchTeam(team1),
                new MatchTeam(team2)
            ),
            kitType,
            false,
            false,
                null
        );

        if (match == null) {
            initiator.sendMessage(ChatColor.RED + "Failed to start team split.");
            return;
        }

        if (spectator != null) {
            match.addSpectator(spectator, null);
        }
    }

    public static void askLeaderToInvite(Party party, Player requester, Player target) {
        requester.sendMessage(net.md_5.bungee.api.ChatColor.YELLOW + "You have requested to invite " + target.getDisplayName() + net.md_5.bungee.api.ChatColor.YELLOW + ".");

        Player leader = Bukkit.getPlayer(party.getLeader());

        // should never happen
        if (leader == null) {
            return;
        }

        leader.sendMessage(requester.getDisplayName() + net.md_5.bungee.api.ChatColor.YELLOW + " wants you to invite " + target.getDisplayName() + net.md_5.bungee.api.ChatColor.YELLOW + ".");
        leader.spigot().sendMessage(createInviteButton(target));
    }

    public static TextComponent createInviteButton(Player target) {
        BaseComponent[] hoverTooltip = { new TextComponent(net.md_5.bungee.api.ChatColor.GREEN + "Click to invite") };
        HoverEvent.Action showText = HoverEvent.Action.SHOW_TEXT;
        ClickEvent.Action runCommand = ClickEvent.Action.RUN_COMMAND;

        TextComponent inviteButton = new TextComponent("Click here to send the invitation");

        inviteButton.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        inviteButton.setHoverEvent(new HoverEvent(showText, hoverTooltip));
        inviteButton.setClickEvent(new ClickEvent(runCommand, "/invite " + target.getName()));

        return inviteButton;
    }

}