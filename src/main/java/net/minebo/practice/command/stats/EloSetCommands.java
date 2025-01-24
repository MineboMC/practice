package net.minebo.practice.command.stats;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.google.common.collect.ImmutableSet;
import net.minebo.practice.Practice;
import net.minebo.practice.profile.elo.EloHandler;
import net.minebo.practice.profile.elo.repository.MongoEloRepository;
import net.minebo.practice.kit.kittype.KitType;
import net.minebo.practice.party.Party;
import net.minebo.practice.party.PartyHandler;

import net.minebo.practice.util.MongoUtils;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.*;

@CommandAlias("elo")
public class EloSetCommands extends BaseCommand {

    @Subcommand("setsolo")
    @Description("Set a player's elo.")
    @CommandPermission("potpvp.admin")
    @CommandCompletion("@players @kittypes")
    @Syntax("<player> <kitType> <elo>")
    public void eloSetSolo(CommandSender sender, OnlinePlayer target, String type, Integer newElo) {

        if (target == null) {
            return;
        }

        KitType kitType = KitType.byId(type);
        EloHandler eloHandler = Practice.getInstance().getEloHandler();
        eloHandler.setElo(target.getPlayer(), kitType, newElo);
        sender.sendMessage(ChatColor.YELLOW + "Set " + target.getPlayer().getName() + "'s " + kitType.getDisplayName() + " elo to " + newElo + ".");
    }

    @Subcommand("setteam")
    @Description("Set a team's elo.")
    @CommandPermission("potpvp.admin")
    @CommandCompletion("@players @kittypes")
    @Syntax("<player> <kitType> <elo>")
    public void eloSetTeam(CommandSender sender, OnlinePlayer target, String type, Integer newElo) {

        if (target == null) {
            return;
        }

        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        EloHandler eloHandler = Practice.getInstance().getEloHandler();
        KitType kitType = KitType.byId(type);

        Party targetParty = partyHandler.getParty(target.getPlayer());

        if (targetParty == null) {
            sender.sendMessage(ChatColor.RED + target.getPlayer().getName() + " is not in a party.");
            return;
        }

        eloHandler.setElo(targetParty.getMembers(), kitType, newElo);
        sender.sendMessage(ChatColor.YELLOW + "Set " + kitType.getDisplayName() + " elo of " + Practice.getInstance().getUuidCache().name(targetParty.getLeader()) + "'s party to " + newElo + ".");
    }

    @Subcommand("recalcglobalelo")
    @Description("Recalculate everyone's global elo.")
    @CommandPermission("potpvp.admin")
    public void recalcGlobalElo(CommandSender sender) {
        List<Document> documents = MongoUtils.getCollection(MongoEloRepository.MONGO_COLLECTION_NAME).find().into(new ArrayList<>());
        sender.sendMessage(ChatColor.GREEN + "Recalculating " + documents.size() + " players global elo...");
        final int[] wrapper = new int[2];
        documents.forEach(document -> {
            try {
                UUID uuid = UUID.fromString((String) document.get("players", ArrayList.class).get(0));
                Set<UUID> uuidSet = ImmutableSet.of(uuid);
                Map<KitType, Integer> eloMap = MongoEloRepository.getInstance().loadElo(uuidSet);
                MongoEloRepository.getInstance().saveElo(uuidSet, eloMap);

                wrapper[0]++;
                if (wrapper[0] % 100 == 0) {
                    sender.sendMessage(ChatColor.GREEN + "Finished " + wrapper[0] + " out of " + documents.size() +" players...");
                }
            } catch (Exception e) {
                e.printStackTrace();
                wrapper[1]++;
            }
        });
    }
}