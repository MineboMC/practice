package net.minebo.practice.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import net.minebo.practice.misc.Lang;
import net.minebo.practice.Practice;
import net.minebo.practice.kit.kittype.menu.select.SelectKitTypeMenu;
import net.minebo.practice.match.MatchHandler;
import net.minebo.practice.match.MatchTeam;
import net.minebo.practice.party.*;
import net.minebo.practice.util.PatchedPlayerUtils;
import net.minebo.practice.misc.Validation;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 4/14/2022
 * Project: potpvp-reprised
 */

@CommandAlias("party|p")
public class PartyCommands extends BaseCommand {

    // default value for password parameter used to detect that password
    // wasn't provided. No Optional<String> :(
    private static final String NO_PASSWORD_PROVIDED = "skasjkdasdjhksahjd";

    private static final List<String> HELP_MESSAGE = ImmutableList.of(
            ChatColor.DARK_PURPLE + Lang.LONG_LINE,
            "§d§lParty Help §7- §fInformation on how to use party commands",
            ChatColor.DARK_PURPLE + Lang.LONG_LINE,
            "§9Party Commands:",
            "§e/party invite §7- Invite a player to join your party",
            "§e/party leave §7- Leave your current party",
            "§e/party accept [player] §7- Accept party invitation",
            "§e/party info [player] §7- View the roster of the party",
            "",
            "§9Leader Commands:",
            "§e/party kick <player> §7- Kick a player from your party",
            "§e/party leader <player> §7- Transfer party leadership",
            "§e/party disband §7 - Disbands party",
            "§e/party lock §7 - Lock party from others joining",
            "§e/party open §7 - Open party to others joining",
            "§e/party password <password> §7 - Sets party password",
            "",
            "§9Other Help:",
            "§eTo use §dparty chat§e, prefix your message with the §7'§d@§7' §esign.",
            ChatColor.DARK_PURPLE + Lang.LONG_LINE
    );

    @HelpCommand
    public void help(CommandSender sender) {
        HELP_MESSAGE.forEach(sender::sendMessage);
    }

    @Subcommand("create")
    @Description("Create a new party.")
    public void partyCreate(Player sender) {
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();

        if (partyHandler.hasParty(sender)) {
            sender.sendMessage(ChatColor.RED + "You are already in a party.");
            return;
        }

        partyHandler.getOrCreateParty(sender);
        sender.sendMessage(ChatColor.YELLOW + "Created a new party.");
    }

    @Subcommand("disband")
    @Description("Disband a new party.")
    public void partyDisband(Player sender) {
        Party party = Practice.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(Lang.NOT_IN_PARTY);
            return;
        }

        if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(Lang.NOT_LEADER_OF_PARTY);
            return;
        }

        party.disband();
    }

    @Subcommand("ffa")
    @Description("Start a party ffa.")
    public void partyFFA(Player sender) {
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        Party party = partyHandler.getParty(sender);

        if (party == null) {
            sender.sendMessage(Lang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(Lang.NOT_LEADER_OF_PARTY);
        } else {
            MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

            if (!Validation.canStartFfa(party, sender)) {
                return;
            }

            new SelectKitTypeMenu(kitType -> {
                sender.closeInventory();

                if (!Validation.canStartFfa(party, sender)) {
                    return;
                }

                List<MatchTeam> teams = new ArrayList<>();

                for ( UUID member : party.getMembers()) {
                    teams.add(new MatchTeam(member));
                }

                matchHandler.startMatch(teams, kitType, false, false);
            }, "Start a Party FFA...").openMenu(sender);
        }
    }

    @Subcommand("i|who|info")
    @Description("View information of your party or another.")
    @CommandCompletion("@players")
    public void partyInfo(Player sender, @Optional() OnlinePlayer target) {
        if (target == null) target = new OnlinePlayer(sender);
        Party party = Practice.getInstance().getPartyHandler().getParty(target.getPlayer());

        if (party == null) {
            if (sender == target) {
                sender.sendMessage(Lang.NOT_IN_PARTY);
            } else {
                sender.sendMessage(ChatColor.RED + target.getPlayer().getName() + " isn't in a party.");
            }
            return;
        }

        String leaderName = Practice.getInstance().getUuidCache().name(party.getLeader());
        int memberCount = party.getMembers().size();
        String members = Joiner.on(", ").join(PatchedPlayerUtils.mapToNames(party.getMembers()));

        sender.sendMessage(ChatColor.GRAY + Lang.LONG_LINE);
        sender.sendMessage(ChatColor.YELLOW + "Leader: " + ChatColor.GOLD + leaderName);
        sender.sendMessage(ChatColor.YELLOW + "Members " + ChatColor.GOLD + "(" + memberCount + ")" + ChatColor.YELLOW + ": " + ChatColor.GRAY + members);

        switch (party.getAccessRestriction()) {
            case PUBLIC:
                sender.sendMessage(ChatColor.YELLOW + "Privacy: " + ChatColor.GREEN + "Open");
                break;
            case INVITE_ONLY:
                sender.sendMessage(ChatColor.YELLOW + "Privacy: " + ChatColor.GOLD + "Invite-Only");
                break;
            case PASSWORD:
                // leader can see password by hovering
                if (party.isLeader(sender.getUniqueId())) {
                    HoverEvent.Action showText = HoverEvent.Action.SHOW_TEXT;
                    BaseComponent[] passwordComponent = { new TextComponent(party.getPassword()) };

                    // Privacy: Password Protected [Hover for password]
                    ComponentBuilder builder = new ComponentBuilder("Privacy: ").color(net.md_5.bungee.api.ChatColor.YELLOW);
                    builder.append("Password Protected ").color(net.md_5.bungee.api.ChatColor.RED);
                    builder.append("[Hover for password]").color(net.md_5.bungee.api.ChatColor.GRAY);
                    builder.event(new HoverEvent(showText, passwordComponent));

                    sender.spigot().sendMessage(builder.create());
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "Privacy: " + ChatColor.RED + "Password Protected");
                }

                break;
            default:
                break;
        }

        sender.sendMessage(ChatColor.GRAY + Lang.LONG_LINE);
    }

    @Subcommand("add|invite")
    @Description("Invite a target to your party.")
    @CommandCompletion("@players")
    public void partyInvite(Player sender, OnlinePlayer target) {
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        Party party = partyHandler.getParty(sender);

        if (target == null) {
            return;
        }

        if (sender == target) {
            sender.sendMessage(ChatColor.RED + "You cannot invite yourself to your own party.");
            return;
        }

        if (sender.hasMetadata("ModMode")) {
            sender.sendMessage(ChatColor.RED + "You cannot do this while in silent mode!");
            return;
        }

        if (party != null) {
            if (party.isMember(target.getPlayer().getUniqueId())) {
                sender.sendMessage(ChatColor.RED + target.getPlayer().getName() + " is already in your party.");
                return;
            }

            if (party.getInvite(target.getPlayer().getUniqueId()) != null) {
                sender.sendMessage(ChatColor.RED + target.getPlayer().getName() + " already has a pending party invite.");
                return;
            }
        }

        if (partyHandler.hasParty(target.getPlayer())) {
            sender.sendMessage(ChatColor.RED + target.getPlayer().getName() + " is already in another party.");
            return;
        }

        // only create party if validations succeed
        party = partyHandler.getOrCreateParty(sender);

        if (party.getMembers().size() >= Party.MAX_SIZE && !sender.isOp()) { // I got the permission from "/party invite **" below
            sender.sendMessage(ChatColor.RED + "Your party has reached the " + Party.MAX_SIZE + " player limit.");
            return;
        }

        if (party.isLeader(sender.getUniqueId())) {
            party.invite(target.getPlayer());
        } else {
            PartyUtils.askLeaderToInvite(party, sender, target.getPlayer());
        }
    }

    @Subcommand("join")
    @Description("Join a party.")
    @CommandCompletion("@players")
    public void partyJoin(Player sender, OnlinePlayer target) {

        if (target == null) {
            return;
        }

        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        Party targetParty = partyHandler.getParty(target.getPlayer());

        if (partyHandler.hasParty(sender)) {
            sender.sendMessage(ChatColor.RED + "You are already in a party. You must leave your current party first.");
            return;
        }

        if (targetParty == null) {
            sender.sendMessage(ChatColor.RED + target.getPlayer().getName() + " is not in a party.");
            return;
        }

        PartyInvite invite = targetParty.getInvite(sender.getUniqueId());

        switch (targetParty.getAccessRestriction()) {
            case PUBLIC:
                targetParty.join(sender);
                break;
            case INVITE_ONLY:
                if (invite != null) {
                    targetParty.join(sender);
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have an invitation to this party.");
                }

                break;
            default:
                break;
        }
    }

    @Subcommand("kick")
    @Description("Kick a player from a party.")
    @CommandCompletion("@players")
    public void kick(Player sender, OnlinePlayer target) {

        if (target == null) {
            return;
        }

        Party party = Practice.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(Lang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(Lang.NOT_LEADER_OF_PARTY);
        } else if (sender == target) {
            sender.sendMessage(ChatColor.RED + "You cannot kick yourself.");
        } else if (!party.isMember(target.getPlayer().getUniqueId())) {
            sender.sendMessage(ChatColor.RED + target.getPlayer().getName() + " isn't in your party.");
        } else {
            party.kick(target.getPlayer());
        }
    }

    @Subcommand("leader|promote")
    @Description("Promote a player in your party to leader.")
    @CommandCompletion("@players")
    public void leader(Player sender, OnlinePlayer target) {
        if (target == null) {
            return;
        }
        Party party = Practice.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(Lang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(Lang.NOT_LEADER_OF_PARTY);
        } else if (!party.isMember(target.getPlayer().getUniqueId())) {
            sender.sendMessage(ChatColor.RED + target.getPlayer().getName() + " isn't in your party.");
        } else if (sender == target.getPlayer()) {
            sender.sendMessage(ChatColor.RED + "You cannot promote yourself to the leader of your own party.");
        } else {
            party.setLeader(target.getPlayer());
        }
    }

    @Subcommand("leave")
    @Description("Leave a party.")
    public void leave(Player sender) {
        Party party = Practice.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(Lang.NOT_IN_PARTY);
        } else {
            party.leave(sender);
        }
    }

    @Subcommand("lock|close")
    @Description("Close your party to the public.")
    public void lock(Player sender) {
        Party party = Practice.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(Lang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(Lang.NOT_LEADER_OF_PARTY);
        } else if (party.getAccessRestriction() == PartyAccessRestriction.INVITE_ONLY) {
            sender.sendMessage(ChatColor.RED + "Your party is already locked.");
        } else {
            party.setAccessRestriction(PartyAccessRestriction.INVITE_ONLY);
            sender.sendMessage(ChatColor.YELLOW + "Your party is now " + ChatColor.RED + "locked" + ChatColor.YELLOW + ".");
        }
    }

    @Subcommand("unlock|open")
    @Description("Open your party to the public.")
    public void unlock(Player sender) {
        Party party = Practice.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(Lang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(Lang.NOT_LEADER_OF_PARTY);
        } else if (party.getAccessRestriction() == PartyAccessRestriction.PUBLIC) {
            sender.sendMessage(ChatColor.RED + "Your party is already open.");
        } else {
            party.setAccessRestriction(PartyAccessRestriction.PUBLIC);
            sender.sendMessage(ChatColor.YELLOW + "Your party is now " + ChatColor.GREEN + "open" + ChatColor.YELLOW + ".");
        }
    }

    @Subcommand("teamsplit|split")
    @Description("Start a team split match.")
    public void teamSplit(Player sender) {
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        Party party = partyHandler.getParty(sender);

        if (party == null) {
            sender.sendMessage(Lang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(Lang.NOT_LEADER_OF_PARTY);
        } else {
            PartyUtils.startTeamSplit(party, sender);
        }
    }
}
