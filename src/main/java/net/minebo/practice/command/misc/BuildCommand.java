package net.minebo.practice.command.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import net.minebo.practice.Practice;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class BuildCommand extends BaseCommand {

    @CommandAlias("build")
    @Description("Toggle build mode.")
    @CommandPermission("potpvp.build")
    public void buiild(Player sender) {
        if (sender.hasMetadata("Build")) {
            sender.removeMetadata("Build", Practice.getInstance());
            sender.sendMessage(ChatColor.RED + "Build mode disabled.");
        } else {
            sender.setMetadata("Build", new FixedMetadataValue(Practice.getInstance(), true));
            sender.sendMessage(ChatColor.GREEN + "Build mode enabled.");
        }
    }

}