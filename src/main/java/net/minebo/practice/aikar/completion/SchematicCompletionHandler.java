package net.minebo.practice.aikar.completion;

import co.aikar.commands.CommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import net.minebo.practice.Practice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SchematicCompletionHandler implements CommandCompletions.CommandCompletionHandler {
    @Override
    public Collection<String> getCompletions(CommandCompletionContext context) throws InvalidCommandArgument {
        List<String> completions = new ArrayList<>();

        Practice.getInstance().getArenaHandler().getSchematics().forEach(a -> {
            completions.add(a.getName());
        });

        return completions;
    }
}
