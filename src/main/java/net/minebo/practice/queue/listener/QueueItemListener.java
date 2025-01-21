package net.minebo.practice.queue.listener;

import com.google.common.collect.ImmutableList;
import net.minebo.practice.Practice;
import net.minebo.practice.kit.kittype.KitType;
import net.minebo.practice.kit.kittype.menu.select.CustomSelectKitTypeMenu;
import net.minebo.practice.match.MatchHandler;
import net.minebo.practice.party.Party;
import net.minebo.practice.queue.QueueHandler;
import net.minebo.practice.queue.QueueItems;
import net.minebo.practice.misc.Validation;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.function.Function;

public final class QueueItemListener implements Listener {

    private final Function<KitType, CustomSelectKitTypeMenu.CustomKitTypeMeta> selectionAdditionRanked = selectionMenuAddition(true);
    private final Function<KitType, CustomSelectKitTypeMenu.CustomKitTypeMeta> selectionAdditionUnranked = selectionMenuAddition(false);
    private final QueueHandler queueHandler;

    public QueueItemListener(QueueHandler queueHandler) {
        this.queueHandler = queueHandler;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getItem() == null) {
            return;
        }

        if (event.getItem().isSimilar(QueueItems.JOIN_SOLO_UNRANKED_QUEUE_ITEM)) {
            joinSoloConsumer(player, false);
        } else if (event.getItem().isSimilar(QueueItems.JOIN_SOLO_RANKED_QUEUE_ITEM)) {
            joinSoloConsumer(player, true);
        } else if (event.getItem().isSimilar(QueueItems.JOIN_PARTY_UNRANKED_QUEUE_ITEM)) {
            joinPartyConsumer(player, false);
        } else if (event.getItem().isSimilar(QueueItems.JOIN_PARTY_RANKED_QUEUE_ITEM)) {
            joinPartyConsumer(player, true);
        } else if (event.getItem().isSimilar(QueueItems.LEAVE_SOLO_UNRANKED_QUEUE_ITEM)) {
            queueHandler.leaveQueue(player, false);
        } else if (event.getItem().isSimilar(QueueItems.LEAVE_SOLO_RANKED_QUEUE_ITEM)) {
            queueHandler.leaveQueue(player, true);
        } else if (event.getItem().isSimilar(QueueItems.LEAVE_PARTY_UNRANKED_QUEUE_ITEM)) {
            leaveQueuePartyConsumer(player);
        } else if (event.getItem().isSimilar(QueueItems.LEAVE_PARTY_RANKED_QUEUE_ITEM)) {
            leaveQueuePartyConsumer(player);
        }
    }

    private void joinSoloConsumer(Player player, boolean ranked) {
        if (Validation.canJoinQueue(player)) {
            new CustomSelectKitTypeMenu(kitType -> {
                queueHandler.joinQueue(player, kitType, ranked);
                player.closeInventory();
            }, ranked ? selectionAdditionRanked : selectionAdditionUnranked, "Join " + (ranked ? "Ranked" : "Unranked") + " Queue...", ranked).openMenu(player);
        }
    }

    private void joinPartyConsumer(Player player, boolean ranked) {
        Party party = Practice.getInstance().getPartyHandler().getParty(player);

        if (party == null || !party.isLeader(player.getUniqueId())) {
            return;
        }

        if (Validation.canJoinQueue(party)) {
            new CustomSelectKitTypeMenu(kitType -> {
                queueHandler.joinQueue(party, kitType, ranked);
                player.closeInventory();
            }, ranked ? selectionAdditionRanked : selectionAdditionUnranked, "Play " + (ranked ? "Ranked" : "Unranked"), ranked).openMenu(player);
        }
    }

    private void leaveQueuePartyConsumer(Player player) {
        Party party = Practice.getInstance().getPartyHandler().getParty(player);

        if (party != null && party.isLeader(player.getUniqueId())) {
            queueHandler.leaveQueue(party, false);
        }
    }

    private Function<KitType, CustomSelectKitTypeMenu.CustomKitTypeMeta> selectionMenuAddition(boolean ranked) {
        return kitType -> {
            MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

            int inFightsRanked = matchHandler.countPlayersPlayingMatches(m -> m.getKitType() == kitType && m.isRanked());
            int inQueueRanked = queueHandler.countPlayersQueued(kitType, true);

            int inFightsUnranked = matchHandler.countPlayersPlayingMatches(m -> m.getKitType() == kitType && !m.isRanked());
            int inQueueUnranked = queueHandler.countPlayersQueued(kitType, false);

            return new CustomSelectKitTypeMenu.CustomKitTypeMeta(
                    Math.max(1, Math.min(64, ranked ? inQueueRanked + inFightsRanked : inQueueUnranked + inFightsUnranked)),
                    ranked ? ImmutableList.of(
                            " ",
                            ChatColor.WHITE + "Fighting: " + ChatColor.YELLOW + inFightsRanked,
                            ChatColor.WHITE + "Queueing: " + ChatColor.YELLOW + inQueueRanked
                    ) :
                            ImmutableList.of(
                                    " ",
                                    ChatColor.WHITE + "Fighting: " + ChatColor.YELLOW + inFightsUnranked,
                                    ChatColor.WHITE + "Queueing: " + ChatColor.YELLOW + inQueueUnranked
                            )
            );
        };
    }
}
