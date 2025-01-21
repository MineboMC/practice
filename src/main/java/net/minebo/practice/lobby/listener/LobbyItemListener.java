package net.minebo.practice.lobby.listener;

import net.minebo.practice.Practice;
import net.minebo.practice.command.misc.ManageCommand;
import net.minebo.practice.command.silent.UnfollowCommand;
import net.minebo.practice.lobby.LobbyHandler;
import net.minebo.practice.lobby.LobbyItems;
import net.minebo.practice.lobby.menu.SpectateMenu;
import net.minebo.practice.lobby.menu.StatisticsMenu;
import net.minebo.practice.match.Match;
import net.minebo.practice.match.MatchHandler;
import net.minebo.practice.match.MatchState;
import net.minebo.practice.misc.Validation;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class LobbyItemListener implements Listener {

    private final Map<UUID, Long> canUseRandomSpecItem = new HashMap<>();
    private final LobbyHandler lobbyHandler;

    public LobbyItemListener(LobbyHandler lobbyHandler) {
        this.lobbyHandler = lobbyHandler;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || !event.getAction().name().contains("RIGHT_")) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item.isSimilar(LobbyItems.MANAGE_ITEM)) {
            handleManageItem(player);
        } else if (item.isSimilar(LobbyItems.DISABLE_SPEC_MODE_ITEM)) {
            handleDisableSpectatorMode(player);
        } else if (item.isSimilar(LobbyItems.ENABLE_SPEC_MODE_ITEM)) {
            handleEnableSpectatorMode(player);
        } else if (item.isSimilar(LobbyItems.SPECTATE_MENU_ITEM)) {
            handleSpectateMenu(player);
        } else if (item.isSimilar(LobbyItems.SPECTATE_RANDOM_ITEM)) {
            handleSpectateRandom(player);
        } else if (item.isSimilar(LobbyItems.PLAYER_STATISTICS)) {
            handlePlayerStatistics(player);
        } else if (item.isSimilar(LobbyItems.UNFOLLOW_ITEM)) {
            handleUnfollow(player);
        }
    }

    private void handleManageItem(Player player) {
        if (player.hasPermission("potpvp.admin")) {
            new ManageCommand().manage(player);
        }
    }

    private void handleDisableSpectatorMode(Player player) {
        if (lobbyHandler.isInLobby(player)) {
            lobbyHandler.setSpectatorMode(player, false);
        }
    }

    private void handleEnableSpectatorMode(Player player) {
        if (lobbyHandler.isInLobby(player) && Validation.canUseSpectateItem(player)) {
            lobbyHandler.setSpectatorMode(player, true);
        }
    }

    private void handleSpectateMenu(Player player) {
        if (Validation.canUseSpectateItemIgnoreMatchSpectating(player)) {
            new SpectateMenu().openMenu(player);
        }
    }

    private void handleSpectateRandom(Player player) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        if (!Validation.canUseSpectateItemIgnoreMatchSpectating(player)) {
            return;
        }

        if (canUseRandomSpecItem.getOrDefault(player.getUniqueId(), 0L) > System.currentTimeMillis()) {
            player.sendMessage(ChatColor.RED + "Please wait before doing this again!");
            return;
        }

        List<Match> matches = new ArrayList<>(matchHandler.getHostedMatches());
        matches.removeIf(m -> m.isSpectator(player.getUniqueId()) || m.getState() == MatchState.ENDING);

        if (matches.isEmpty()) {
            player.sendMessage(ChatColor.RED + "There are no matches available to spectate.");
        } else {
            Match currentlySpectating = matchHandler.getMatchSpectating(player);
            Match newSpectating = matches.get(ThreadLocalRandom.current().nextInt(matches.size()));

            if (currentlySpectating != null) {
                currentlySpectating.removeSpectator(player, false);
            }

            newSpectating.addSpectator(player, null);
            canUseRandomSpecItem.put(player.getUniqueId(), System.currentTimeMillis() + 3_000L);
        }
    }

    private void handlePlayerStatistics(Player player) {
        new StatisticsMenu().openMenu(player);
    }

    private void handleUnfollow(Player player) {
        new UnfollowCommand().unfollow(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        canUseRandomSpecItem.remove(event.getPlayer().getUniqueId());
    }
}