package net.minebo.practice.command.silent;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import net.minebo.practice.Practice;
import net.minebo.practice.util.VisibilityUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class SilentCommand extends BaseCommand {

    @CommandAlias("silent")
    @Description("Toggle silent mode.")
    @CommandPermission("potpvp.staff")
    public void silent(Player sender) {
        if (sender.hasMetadata("ModMode")) {
            sender.removeMetadata("ModMode", Practice.getInstance());
            sender.removeMetadata("invisible", Practice.getInstance());

            sender.sendMessage(ChatColor.RED + "Silent mode disabled.");
        } else {
            sender.setMetadata("ModMode", new FixedMetadataValue(Practice.getInstance(), true));
            sender.setMetadata("invisible", new FixedMetadataValue(Practice.getInstance(), true));
            
            sender.sendMessage(ChatColor.GREEN + "Silent mode enabled.");
        }

        VisibilityUtils.updateVisibility(sender);
    }

}