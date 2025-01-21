package net.minebo.practice.aikar.completion;

import co.aikar.commands.CommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import net.minebo.practice.Practice;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlayerCompletionHandler implements CommandCompletions.CommandCompletionHandler {
    @Override
    public Collection<String> getCompletions(CommandCompletionContext context) throws InvalidCommandArgument {
        List<String> completions = new ArrayList<>();

        Bukkit.getOnlinePlayers().forEach(a -> {
            if(!a.hasMetadata("vanished")){
                completions.add(a.getName());
            }
        });

        return completions;
    }
}
