package net.minebo.practice.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.minebo.practice.misc.Lang;
import net.minebo.practice.Practice;
import net.minebo.practice.kit.kittype.KitType;
import net.minebo.practice.match.Match;
import net.minebo.practice.match.MatchState;
import net.minebo.practice.match.MatchTeam;
import net.minebo.practice.party.Party;
import net.minebo.practice.tournament.Tournament;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 4/4/2022
 * Project: potpvp-reprised
 */

@CommandAlias("tourny|tournament|t")
public class TournamentCommands extends BaseCommand {

    @Subcommand("start")
    @Description("Starts a tournament.")
    @CommandPermission("potpvp.admin")
    @CommandCompletion("@kittypes <size> <teams>")
    public void tournamentCreate(CommandSender sender, String kittype, int teamSize, int requiredTeams) {
        if (Practice.getInstance().getTournamentHandler().getTournament() != null) {
            sender.sendMessage(ChatColor.RED + "There's already an ongoing tournament!");
            return;
        }

        KitType type = KitType.byId(kittype);

        if (type == null) {
            sender.sendMessage(ChatColor.RED + "Kit type not found!");
            return;
        }

        if (teamSize < 1 || 10 < teamSize) {
            sender.sendMessage(ChatColor.RED + "Invalid team size range. Acceptable inputs: 1 -> 10");
            return;
        }

        if (requiredTeams < 4) {
            sender.sendMessage(ChatColor.RED + "Required teams must be at least 4.");
            return;
        }

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7A &6&ltournament&7 has started. Type &e/t join&7 to play. &e(0/" + (teamSize < 3 ? teamSize * requiredTeams : requiredTeams) + ")"));
        Bukkit.broadcastMessage("");

        Tournament tournament;
        Practice.getInstance().getTournamentHandler().setTournament(tournament = new Tournament(type, teamSize, requiredTeams));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (Practice.getInstance().getTournamentHandler().getTournament() == tournament) {
                    tournament.broadcastJoinMessage();
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(Practice.getInstance(), 60 * 20, 60 * 20);
    }

    @Subcommand("join")
    @Description("Joins a tournament.")
    public void tournamentJoin(CommandSender sender) {
        if (Practice.getInstance().getTournamentHandler().getTournament() == null) {
            sender.sendMessage(ChatColor.RED + "There is no running tournament to join.");
            return;
        }

        int tournamentTeamSize = Practice.getInstance().getTournamentHandler().getTournament().getRequiredPartySize();

        if ((Practice.getInstance().getTournamentHandler().getTournament().getCurrentRound() != -1 || Practice.getInstance().getTournamentHandler().getTournament().getBeginNextRoundIn() != 31) && (Practice.getInstance().getTournamentHandler().getTournament().getCurrentRound() != 0 || !sender.hasPermission("tournaments.joinduringcountdown"))) {
            sender.sendMessage(ChatColor.RED + "This tournament is already in progress.");
            return;
        }

        Party senderParty = Practice.getInstance().getPartyHandler().getParty(((Player) sender));
        if (senderParty == null) {
            if (tournamentTeamSize == 1) {
                senderParty = Practice.getInstance().getPartyHandler().getOrCreateParty(((Player) sender)); // Will auto put them in a party
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have a team to join the tournament with!");
                return;
            }
        }

        int notInLobby = 0;
        int queued = 0;
        for ( UUID member : senderParty.getMembers()) {
            if (!Practice.getInstance().getLobbyHandler().isInLobby(Bukkit.getPlayer(member))) {
                notInLobby++;
            }

            if (Practice.getInstance().getQueueHandler().getQueueEntry(member) != null) {
                queued++;
            }
        }

        if (notInLobby != 0) {
            sender.sendMessage(ChatColor.RED.toString() + notInLobby + "member" + (notInLobby == 1 ? "" : "s") + " of your team aren't in the lobby.");
            return;
        }

        if (queued != 0) {
            sender.sendMessage(ChatColor.RED.toString() + notInLobby + "member" + (notInLobby == 1 ? "" : "s") + " of your team are currently queued.");
            return;
        }

        if (!senderParty.getLeader().equals(((Player) sender).getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You must be the leader of your team to join the tournament.");
            return;
        }

        if (Practice.getInstance().getTournamentHandler().isInTournament(senderParty)) {
            sender.sendMessage(ChatColor.RED + "Your team is already in the tournament!");
            return;
        }

        if (senderParty.getMembers().size() != Practice.getInstance().getTournamentHandler().getTournament().getRequiredPartySize()) {
            sender.sendMessage(ChatColor.RED + "You need exactly " + Practice.getInstance().getTournamentHandler().getTournament().getRequiredPartySize() + " members in your party to join the tournament.");
            return;
        }

        if (Practice.getInstance().getQueueHandler().getQueueEntry(senderParty) != null) {
            sender.sendMessage(ChatColor.RED + "You can't join the tournament if your party is currently queued.");
            return;
        }

        senderParty.message(ChatColor.GREEN + "Joined the tournament.");
        Practice.getInstance().getTournamentHandler().getTournament().addParty(senderParty);
    }

    @Subcommand("status")
    @Description("View a tournament's status.")
    public void tournamentStatus(CommandSender sender) {
        if (Practice.getInstance().getTournamentHandler().getTournament() == null) {
            sender.sendMessage(ChatColor.RED + "There is no ongoing tournament to get the status of.");
            return;
        }

        sender.sendMessage(Lang.LONG_LINE);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Tournament Matches"));
        sender.sendMessage("");
        List<Match> ongoingMatches = Practice.getInstance().getTournamentHandler().getTournament().getMatches().stream().filter(m -> m.getState() != MatchState.TERMINATED).collect(Collectors.toList());

        for (Match match : ongoingMatches) {
            MatchTeam firstTeam = match.getTeams().get(0);
            MatchTeam secondTeam = match.getTeams().get(1);

            if (firstTeam.getAllMembers().size() == 1) {
                sender.sendMessage(Bukkit.getPlayer(Practice.getInstance().uuidCache.name(firstTeam.getFirstMember())).getDisplayName() + ChatColor.GRAY + " vs " + Bukkit.getPlayer(Practice.getInstance().uuidCache.name(secondTeam.getFirstMember())).getDisplayName());
            } else {
                sender.sendMessage(Bukkit.getPlayer(Practice.getInstance().uuidCache.name(firstTeam.getFirstMember())).getDisplayName() + ChatColor.GRAY + "'s team vs " + Bukkit.getPlayer(Practice.getInstance().uuidCache.name(secondTeam.getFirstMember())).getDisplayName() + ChatColor.GRAY + "'s team");
            }
        }
        sender.sendMessage(Lang.LONG_LINE);
    }

    @Subcommand("cancel")
    @Description("Cancels an ongoing tournament.")
    @CommandPermission("potpvp.admin")
    public void tournamentCancel(CommandSender sender) {
        if (Practice.getInstance().getTournamentHandler().getTournament() == null) {
            sender.sendMessage(ChatColor.RED + "There is no running tournament to cancel.");
            return;
        }

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7The &c&ltournament&7 was &ccancelled."));
        Bukkit.broadcastMessage("");
        Practice.getInstance().getTournamentHandler().setTournament(null);
    }

    @Subcommand("forcestart")
    @Description("Force start an ongoing tournament.")
    @CommandPermission("potpvp.admin")
    public void tournamentForceStart(CommandSender sender) {
        if (Practice.getInstance().getTournamentHandler().getTournament() == null) {
            sender.sendMessage(ChatColor.RED + "There is no tournament to force start.");
            return;
        }

        if (Practice.getInstance().getTournamentHandler().getTournament().getCurrentRound() != -1 || Practice.getInstance().getTournamentHandler().getTournament().getBeginNextRoundIn() != 31) {
            sender.sendMessage(ChatColor.RED + "This tournament is already in progress.");
            return;
        }

        Practice.getInstance().getTournamentHandler().getTournament().start();
        sender.sendMessage(ChatColor.GREEN + "Force started tournament.");
    }
}
