package net.minebo.practice.util.nametags.listener;

import net.minebo.practice.util.nametags.NameTagHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

public final class NameTagListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().setMetadata("NT-LoggedIn", new FixedMetadataValue(NameTagHandler.getPlugin(), true));

        NameTagHandler.initiatePlayer(event.getPlayer());
        NameTagHandler.reloadPlayer(event.getPlayer());
        NameTagHandler.reloadOthersFor(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.getPlayer().removeMetadata("NT-LoggedIn", NameTagHandler.getPlugin());
        NameTagHandler.getTeamMap().remove(event.getPlayer().getName());
    }

}