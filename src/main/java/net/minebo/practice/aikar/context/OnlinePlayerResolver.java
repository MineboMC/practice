package net.minebo.practice.aikar.context;

import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import co.aikar.commands.contexts.ContextResolver;
import co.aikar.commands.BukkitCommandExecutionContext;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class OnlinePlayerResolver implements ContextResolver<OnlinePlayer, BukkitCommandExecutionContext> {
    @Override
    public OnlinePlayer getContext(BukkitCommandExecutionContext c) {
        String input = c.popFirstArg();
        if (input == null) {
            return null;
        }

        Player target = Bukkit.getPlayerExact(input);
        if (target != null) {
            // Always return OnlinePlayer regardless of vanish
            if(!target.hasMetadata("vanished")){
                return new OnlinePlayer(target);
            }
            return null; // Vanished
        }

        return null; // Player not found
    }
}
