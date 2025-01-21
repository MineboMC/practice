package net.minebo.practice.aikar.context;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import net.minebo.practice.kit.kittype.KitType;

import java.util.UUID;

public class UUIDContextResolver implements ContextResolver<UUID, BukkitCommandExecutionContext> {

    @Override
    public UUID getContext(BukkitCommandExecutionContext bukkitCommandExecutionContext) throws InvalidCommandArgument {
        return UUID.fromString(bukkitCommandExecutionContext.getFirstArg());
    }

}