package net.minebo.practice.util;

import net.minebo.practice.Practice;
import net.minebo.practice.events.EventHandler;
import net.minebo.practice.profile.follow.FollowHandler;
import net.minebo.practice.match.Match;
import net.minebo.practice.match.MatchHandler;
import net.minebo.practice.party.Party;
import net.minebo.practice.party.PartyHandler;
import net.minebo.practice.profile.setting.Setting;
import net.minebo.practice.profile.setting.SettingHandler;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class VisibilityUtils {

    public void updateVisibilityFlicker(Player target) {
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            target.hidePlayer(otherPlayer);
            otherPlayer.hidePlayer(target);
            showPlayerInTab(target, otherPlayer);
        }

        Bukkit.getScheduler().runTaskLater(Practice.getInstance(), () -> updateVisibility(target), 10L);
    }

    public void updateVisibility(Player target) {
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if (shouldSeePlayer(otherPlayer, target)) {
                otherPlayer.showPlayer(target);
            } else {
                otherPlayer.hidePlayer(target);
                showPlayerInTab(target, otherPlayer);
            }

            if (shouldSeePlayer(target, otherPlayer)) {
                target.showPlayer(otherPlayer);
            } else {
                target.hidePlayer(otherPlayer);
                showPlayerInTab(target, otherPlayer);
            }
        }
    }

    private static boolean shouldSeePlayer(Player viewer, Player target) {
        SettingHandler settingHandler = Practice.getInstance().getSettingHandler();
        FollowHandler followHandler = Practice.getInstance().getFollowHandler();
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        if(Practice.getInstance().getLobbyHandler().isInLobby(viewer) && Practice.getInstance().getLobbyHandler().isInLobby(target) && target.hasPermission("potpvp.donor") && !target.hasMetadata("invisible")){
            return true;
        }

        Match targetMatch = matchHandler.getMatchPlayingOrSpectating(target);

        if (targetMatch == null) {
            // we're not in a match so we hide other players based on their party/match
            Party targetParty = partyHandler.getParty(target);
            Optional<UUID> following = followHandler.getFollowing(viewer);

            boolean viewerInEvent = (EventHandler.getCurrentEvent() == null) ? false : EventHandler.getCurrentEvent().isPlayerInEvent(viewer.getUniqueId());
            boolean viewerPlayingMatch = matchHandler.isPlayingOrSpectatingMatch(viewer);
            boolean viewerSameParty = targetParty != null && targetParty.isMember(viewer.getUniqueId());
            boolean viewerFollowingTarget = following.isPresent() && following.get().equals(target.getUniqueId());

            return viewerPlayingMatch || viewerSameParty || viewerFollowingTarget || viewerInEvent;
        } else {
            // we're in a match so we only hide other spectators (if our settings say so)
            boolean targetIsSpectator = targetMatch.isSpectator(target.getUniqueId());
            boolean viewerSpecSetting = settingHandler.getSetting(viewer, Setting.VIEW_OTHER_SPECTATORS);
            boolean viewerIsSpectator = matchHandler.isSpectatingMatch(viewer);

            return !targetIsSpectator || (viewerSpecSetting && viewerIsSpectator && !target.hasMetadata("ModMode"));
        }
    }

    public static void showPlayerInTab(Player toPlayer, Player hiddenPlayer) {
        EntityPlayer hiddenEntity = ((CraftPlayer) hiddenPlayer).getHandle();
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,
                hiddenEntity
        );

        ((CraftPlayer) toPlayer).getHandle().playerConnection.sendPacket(packet);
    }

    public static void hidePlayerFromTab(Player toPlayer, Player hiddenPlayer) {
        EntityPlayer hiddenEntity = ((CraftPlayer) hiddenPlayer).getHandle();
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,
                hiddenEntity
        );

        ((CraftPlayer) toPlayer).getHandle().playerConnection.sendPacket(packet);
    }

}