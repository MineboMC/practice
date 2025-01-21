package net.minebo.practice.kit.listener;

import net.minebo.practice.Practice;
import net.minebo.practice.kit.KitItems;
import net.minebo.practice.kit.menu.kits.KitsMenu;
import net.minebo.practice.kit.kittype.menu.select.SelectKitTypeMenu;
import net.minebo.practice.lobby.LobbyHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public final class KitItemListener implements Listener {

    public KitItemListener() {
        // No need to register handlers, we'll do it manually in the event handler.
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getItem() == null) {
            return;
        }

        // Handle the editor item interaction
        if (event.getItem().isSimilar(KitItems.OPEN_EDITOR_ITEM)) {
            LobbyHandler lobbyHandler = Practice.getInstance().getLobbyHandler();

            if (lobbyHandler.isInLobby(player)) {
                new SelectKitTypeMenu(kitType -> {
                    new KitsMenu(kitType).openMenu(player);
                }, "Select a kit to edit...").openMenu(player);
            }
        }
    }
}
