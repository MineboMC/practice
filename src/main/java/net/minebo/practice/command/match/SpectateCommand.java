package net.minebo.practice.command.match;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import net.minebo.practice.Practice;
import net.minebo.practice.match.Match;
import net.minebo.practice.match.MatchHandler;
import net.minebo.practice.match.MatchTeam;
import net.minebo.practice.profile.setting.Setting;
import net.minebo.practice.profile.setting.SettingHandler;
import net.minebo.practice.misc.Validation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class  SpectateCommand extends BaseCommand {

    private static final int SPECTATE_COOLDOWN_SECONDS = 2;
    private static final Map<UUID, Long> cooldowns = new HashMap<>();

    @CommandAlias("spectate|spec")
    @Description("Spectate a player's match.")
    @CommandCompletion("@players")
    public void spectate(Player sender, OnlinePlayer target) {

        if (target == null) {
            return;
        }

        if (sender == target) {
            sender.sendMessage(ChatColor.RED + "You cannot spectate yourself.");
            return;
        } else if (cooldowns.containsKey(sender.getUniqueId()) && cooldowns.get(sender.getUniqueId()) > System.currentTimeMillis()) {
            sender.sendMessage(ChatColor.RED + "Please wait before using this command again.");
            return;
        }

        cooldowns.put(sender.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(SPECTATE_COOLDOWN_SECONDS));

        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        SettingHandler settingHandler = Practice.getInstance().getSettingHandler();

        Match targetMatch = matchHandler.getMatchPlayingOrSpectating(target.getPlayer());

        if (targetMatch == null) {
            sender.sendMessage(ChatColor.RED + target.getPlayer().getName() + " is not in a match.");
            return;
        }

        //boolean bypassesSpectating = PotPvPRP.getInstance().getTournamentHandler().isInTournament(targetMatch);
        boolean bypassesSpectating = false;

        // only check the seting if the target is actually playing in the match
        if (!bypassesSpectating && (targetMatch.getTeam(target.getPlayer().getUniqueId()) != null && !settingHandler.getSetting(target.getPlayer(), Setting.ALLOW_SPECTATORS))) {
            if (sender.isOp() || sender.hasPermission("potpvp.spectate")) {
                sender.sendMessage(ChatColor.RED + "Bypassing " + target.getPlayer().getName() + "'s no spectators preference...");
            } else {
                sender.sendMessage(ChatColor.RED + target.getPlayer().getName() + " doesn't allow spectators at the moment.");
                return;
            }
        }

        if ((!sender.isOp() && !sender.hasPermission("potpvp.spectate")) && targetMatch.getTeams().size() == 2 && !bypassesSpectating) {
            MatchTeam teamA = targetMatch.getTeams().get(0);
            MatchTeam teamB = targetMatch.getTeams().get(1);

            if (teamA.getAllMembers().size() == 1 && teamB.getAllMembers().size() == 1) {
                UUID teamAPlayer = teamA.getFirstMember();
                UUID teamBPlayer = teamB.getFirstMember();

                if (
                    !settingHandler.getSetting(Bukkit.getPlayer(teamAPlayer), Setting.ALLOW_SPECTATORS) ||
                    !settingHandler.getSetting(Bukkit.getPlayer(teamBPlayer), Setting.ALLOW_SPECTATORS)
                ) {
                    sender.sendMessage(ChatColor.RED + "Not all players in that 1v1 have spectators enabled.");
                    return;
                }
            }
        }

        Player teleportTo = null;

        // /spectate looks up matches being played OR watched by the target,
        // so we can only target them if they're not spectating
        if (!targetMatch.isSpectator(target.getPlayer().getUniqueId())) {
            teleportTo = target.getPlayer();
        }

        if (Validation.canUseSpectateItemIgnoreMatchSpectating(sender)) {
            Match currentlySpectating = matchHandler.getMatchSpectating(sender);

            if (currentlySpectating != null) {
                if (currentlySpectating.equals(targetMatch)) {
                    sender.sendMessage(ChatColor.RED + "You're already spectating this match.");
                    return;
                }

                currentlySpectating.removeSpectator(sender);
            }

            targetMatch.addSpectator(sender, teleportTo);
        }
    }

}