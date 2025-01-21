package net.minebo.practice.arena.listener;

import net.minebo.practice.arena.Arena;
import net.minebo.practice.arena.event.ArenaReleasedEvent;
import net.minebo.practice.util.Cuboid;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Remove dropped items when {@link Arena}s are released.
 */
public final class ArenaItemResetListener implements Listener {

    @EventHandler
    public void onArenaReleased(ArenaReleasedEvent event) {
        Cuboid bounds = event.getArena().getBounds();

        // force load all chunks (can't iterate entities in an unload chunk)
        // that are at all covered by this map.
        bounds.getChunks().forEach(chunk -> {
            chunk.load();

            for (Entity entity : chunk.getEntities()) {
                // if we remove all entities we might call .remove()
                // on a player (breaks a lot of things)
                if (entity instanceof Item && bounds.contains(entity)) {
                    entity.remove();
                }
            }
        });
    }

}