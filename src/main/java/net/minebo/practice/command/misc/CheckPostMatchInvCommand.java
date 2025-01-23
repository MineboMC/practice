package net.minebo.practice.command.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import net.minebo.practice.Practice;
import net.minebo.practice.match.postmatchinv.PostMatchInvHandler;
import net.minebo.practice.match.postmatchinv.PostMatchPlayer;
import net.minebo.practice.match.postmatchinv.menu.PostMatchMenu;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;

import net.minebo.practice.util.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class CheckPostMatchInvCommand extends BaseCommand {

    @CommandAlias("checkPostMatchInv|_")
    @Description("View match data.")
    @CommandCompletion("@players")
    public void checkPostMatchInv(CommandSender sender, OnlinePlayer target) {
        PostMatchInvHandler postMatchInvHandler = Practice.getInstance().getPostMatchInvHandler();
        Map<UUID, PostMatchPlayer> players = postMatchInvHandler.getPostMatchData(Bukkit.getPlayer(sender.getName()).getUniqueId());
        PostMatchPlayer inv = players.get(target.getPlayer().getUniqueId());

        if (inv == null) {
            String name = Practice.getInstance().getUuidCache().name(target.getPlayer().getUniqueId());
            sender.sendMessage(ChatColor.RED + "Data for " + name + " not found.");
        }

        Menu menu = new PostMatchMenu(players.get(target.getPlayer().getUniqueId()));
        menu.openMenu((Player)sender);

    }

}