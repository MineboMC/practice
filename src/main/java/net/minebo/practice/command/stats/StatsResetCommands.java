package net.minebo.practice.command.stats;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import net.minebo.practice.util.menu.menus.ConfirmMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.minebo.practice.Practice;

public class StatsResetCommands extends BaseCommand {

    @CommandAlias("statsreset")
    @Description("Reset a target's stats.")
    @CommandPermission("potpvp.admin")
    @CommandCompletion("@players")
    public void reset(Player sender, OnlinePlayer target) {

        if (target == null) {
            return;
        }

        Bukkit.getScheduler().runTask(Practice.getInstance(), () -> {
            new ConfirmMenu("Stats reset", (reset) -> {
                if (reset) {
                    Bukkit.getScheduler().runTaskAsynchronously(Practice.getInstance(), () -> {
                        Practice.getInstance().getEloHandler().resetElo(target.getPlayer().getUniqueId());
                        sender.sendMessage(ChatColor.GREEN + "Reset the target's stats!");
                    });
                } else {
                    sender.sendMessage(ChatColor.RED + "Stats reset aborted.");
                }
            }).openMenu(sender);
        });
    }
}
