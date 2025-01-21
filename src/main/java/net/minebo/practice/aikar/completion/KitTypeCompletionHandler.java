package net.minebo.practice.aikar.completion;

import co.aikar.commands.CommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import net.minebo.practice.kit.kittype.KitType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KitTypeCompletionHandler implements CommandCompletions.CommandCompletionHandler {
    @Override
    public Collection<String> getCompletions(CommandCompletionContext context) throws InvalidCommandArgument {
        List<String> completions = new ArrayList<>();

        KitType.getAllTypes().forEach(a -> {
            completions.add(a.getId());
        });

        return completions;
    }
}
