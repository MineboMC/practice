package net.minebo.practice.aikar.context;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import org.bukkit.ChatColor;

public class ChatColorContextResolver implements ContextResolver<ChatColor, BukkitCommandExecutionContext> {

    @Override
    public ChatColor getContext(BukkitCommandExecutionContext bukkitCommandExecutionContext) throws InvalidCommandArgument {
        return ChatColor.valueOf(bukkitCommandExecutionContext.getFirstArg());
    }

}
