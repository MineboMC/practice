package net.minebo.practice.nametag;

import net.minebo.basalt.api.BasaltAPI;
import net.minebo.practice.Practice;
import net.minebo.practice.match.Match;
import net.minebo.practice.match.MatchHandler;
import net.minebo.practice.match.MatchTeam;
import net.minebo.practice.profile.follow.FollowHandler;
import net.minebo.practice.pvpclasses.pvpclasses.ArcherClass;
import net.minebo.practice.util.nametags.construct.NameTagInfo;
import net.minebo.practice.util.nametags.provider.NameTagProvider;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public final class NameTagAdapter extends NameTagProvider {

    public NameTagAdapter() {
        super("Practice Provider", 5);
    }

    @Override
    public NameTagInfo fetchNameTag(Player toRefresh, Player refreshFor) {
        String prefixColor = getNameColor(toRefresh, refreshFor);
        return createNameTag(ChatColor.translate(prefixColor), "");
    }

    public static String getNameColor(Player toRefresh, Player refreshFor) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        if (matchHandler.isPlayingOrSpectatingMatch(toRefresh)) {
            return getNameColorMatch(toRefresh, refreshFor);
        } else {
            return getNameColorLobby(toRefresh, refreshFor);
        }
    }

    private static String getNameColorMatch(Player toRefresh, Player refreshFor) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        Match toRefreshMatch = matchHandler.getMatchPlayingOrSpectating(toRefresh);
        MatchTeam toRefreshTeam = toRefreshMatch.getTeam(toRefresh.getUniqueId());

        // they're a spectator, so we see them as gray
        if (toRefreshTeam == null) {
            return ChatColor.GRAY.toString();
        }

        MatchTeam refreshForTeam = toRefreshMatch.getTeam(refreshFor.getUniqueId());

        // if we can't find a current team, check if they have any
        // previously teams we can use for this
        if (refreshForTeam == null) {
            refreshForTeam = toRefreshMatch.getPreviousTeam(refreshFor.getUniqueId());
        }

        // if we were/are both on teams display a friendly/enemy color
        if (refreshForTeam != null) {
            if (toRefreshTeam == refreshForTeam) {
                return ChatColor.GREEN.toString();
            } else {
                if (ArcherClass.getMarkedPlayers().containsKey(toRefresh.getName()) && System.currentTimeMillis() < ArcherClass.getMarkedPlayers().get(toRefresh.getName())) {
                    return ChatColor.YELLOW.toString();
                }
                return ChatColor.RED.toString();
            }
        }

        // if we're a spectator just display standard colors
        List<MatchTeam> teams = toRefreshMatch.getTeams();

        // we have predefined colors for 'normal' matches
        if (teams.size() == 2) {
            // team 1 = RED, team 2 = AQUA
            if (toRefreshTeam == teams.get(0)) {
                return ChatColor.RED.toString();
            } else {
                return ChatColor.AQUA.toString();
            }
        } else {
            // we don't have colors defined for larger matches
            // everyone is just red for spectators
            return ChatColor.RED.toString();
        }
    }

    private static String getNameColorLobby(Player toRefresh, Player refreshFor) {
        FollowHandler followHandler = Practice.getInstance().getFollowHandler();

        Optional<UUID> following = followHandler.getFollowing(refreshFor);
        boolean refreshForFollowingTarget = following.isPresent() && following.get().equals(toRefresh.getUniqueId());

        if (refreshForFollowingTarget) {
            return ChatColor.AQUA.toString();
        } else {
            try {
                return BasaltAPI.INSTANCE.quickFindProfile(toRefresh.getUniqueId()).get().getHighestGlobalRank().getColor();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}