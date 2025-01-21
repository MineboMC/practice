package net.minebo.practice.aikar.context;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import net.minebo.practice.kit.kittype.KitType;

public class KitTypeContextResolver implements ContextResolver<KitType, BukkitCommandExecutionContext> {

    @Override
    public KitType getContext(BukkitCommandExecutionContext bukkitCommandExecutionContext) throws InvalidCommandArgument {
        return KitType.byId(bukkitCommandExecutionContext.getFirstArg());
    }

}
