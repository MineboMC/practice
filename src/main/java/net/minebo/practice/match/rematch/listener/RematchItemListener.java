package net.minebo.practice.match.rematch.listener;

import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import net.minebo.practice.Practice;
import net.minebo.practice.command.duel.AcceptCommand;
import net.minebo.practice.command.duel.DuelCommand;
import net.minebo.practice.match.rematch.RematchData;
import net.minebo.practice.match.rematch.RematchHandler;
import net.minebo.practice.match.rematch.RematchItems;
import net.minebo.practice.util.InventoryUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public final class RematchItemListener implements Listener {

    public RematchItemListener(RematchHandler rematchHandler) {
        // No need to register handlers here, we will handle them in the event methods.
    }

    @EventHandler
    public void onRematchRequest(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getItem() == null) {
            return;
        }

        if (event.getItem().isSimilar(RematchItems.REQUEST_REMATCH_ITEM)) {
            RematchData rematchData = Practice.getInstance().getRematchHandler().getRematchData(player);

            if (rematchData != null) {
                Player target = Bukkit.getPlayer(rematchData.getTarget());
                if (target != null) {
                    new DuelCommand().duel(player, (OnlinePlayer) target, rematchData.getKitType());

                    InventoryUtils.resetInventoryDelayed(player);
                    InventoryUtils.resetInventoryDelayed(target);
                }
            }
        } else if (event.getItem().isSimilar(RematchItems.SENT_REMATCH_ITEM)) {
            player.sendMessage(ChatColor.RED + "You have already sent a rematch request.");
        } else if (event.getItem().isSimilar(RematchItems.ACCEPT_REMATCH_ITEM)) {
            RematchData rematchData = Practice.getInstance().getRematchHandler().getRematchData(player);

            if (rematchData != null) {
                Player target = Bukkit.getPlayer(rematchData.getTarget());
                if (target != null) {
                    new AcceptCommand().accept(player, (OnlinePlayer) target);
                }
            }
        }
    }
}
