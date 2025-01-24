package net.minebo.practice.aikar.context;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import co.aikar.commands.contexts.ContextResolver;
import co.aikar.commands.BukkitCommandExecutionContext;
import net.minebo.basalt.util.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class OnlinePlayerResolver implements ContextResolver<OnlinePlayer, BukkitCommandExecutionContext> {
    @Override
    public OnlinePlayer getContext(BukkitCommandExecutionContext c) {
        String input = c.popFirstArg();

        Player target = Bukkit.getPlayerExact(input);
        if (target != null) {
            // Always return OnlinePlayer regardless of vanish
            if(!target.hasMetadata("vanished")){
                return new OnlinePlayer(target);
            }
            throw new InvalidCommandArgument(ChatColor.RED + input + " is not online."); // Vanished
        }

        throw new InvalidCommandArgument(ChatColor.RED + input + " is not online.");
    }
}
