package net.minebo.practice.command.duel;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import net.minebo.practice.arena.menu.select.SelectArenaMenu;
import net.minebo.practice.misc.Lang;
import net.minebo.practice.Practice;
import net.minebo.practice.match.duel.DuelHandler;
import net.minebo.practice.match.duel.DuelInvite;
import net.minebo.practice.match.duel.PartyDuelInvite;
import net.minebo.practice.match.duel.PlayerDuelInvite;
import net.minebo.practice.kit.kittype.KitType;
import net.minebo.practice.kit.kittype.menu.select.SelectKitTypeMenu;
import net.minebo.practice.lobby.LobbyHandler;
import net.minebo.practice.party.Party;
import net.minebo.practice.party.PartyHandler;
import net.minebo.practice.misc.Validation;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
@CommandAlias("duel|fight")
public final class DuelCommand extends BaseCommand {

    @Default
    @Description("Duel a player.")
    @CommandCompletion("@players")
    public void duel(CommandSender sender, OnlinePlayer target) {

        if (target == null) {
            return;
        }

        if (sender == target.getPlayer()) {
            sender.sendMessage(ChatColor.RED + "You can't duel yourself!");
            return;
        }

        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        LobbyHandler lobbyHandler = Practice.getInstance().getLobbyHandler();

        Party senderParty = partyHandler.getParty((Player) sender);
        Party targetParty = partyHandler.getParty(target.getPlayer());

        if (senderParty != null && targetParty != null) {
            // party dueling party (legal)
            if (!Validation.canSendDuel(senderParty, targetParty, (Player) sender)) {
                return;
            }

            new SelectKitTypeMenu(kitType -> {
                new SelectArenaMenu(kitType, arenaName -> {
                    ((Player) sender).closeInventory();

                    // Reassign these fields so that any party changes (kicks, etc.) are reflected
                    Party newSenderParty = partyHandler.getParty(((Player) sender));
                    Party newTargetParty = partyHandler.getParty(target.getPlayer());

                    if (newSenderParty != null && newTargetParty != null) {
                        if (newSenderParty.isLeader(((Player) sender).getUniqueId())) {
                            duel(((Player) sender), newSenderParty, newTargetParty, kitType, arenaName);
                        } else {
                            sender.sendMessage(Lang.NOT_LEADER_OF_PARTY);
                        }
                    }
                }).openMenu((Player) sender);
            }, "Select a ladder...").openMenu(((Player) sender));

        } else if (senderParty == null && targetParty == null) {
            // player dueling player (legal)
            if (!Validation.canSendDuel(((Player) sender), target.getPlayer())) {
                return;
            }

            if (target.getPlayer().hasPermission("potpvp.famous") && System.currentTimeMillis() - lobbyHandler.getLastLobbyTime(target.getPlayer()) < 3_000) {
                sender.sendMessage(ChatColor.RED + target.getPlayer().getName() + " just returned to the lobby, please wait a moment.");
                return;
            }

            new SelectKitTypeMenu(kitType -> {
                new SelectArenaMenu(kitType, arenaName -> {
                    ((Player) sender).closeInventory();
                    duel((Player) sender, target, kitType, arenaName);
                }).openMenu((Player) sender);
            }, "Select a kit type...").openMenu(((Player) sender));
        } else if (senderParty == null) {
            // player dueling party (illegal)
            sender.sendMessage(ChatColor.RED + "You must create a party to duel " + target.getPlayer().getName() + "'s party.");
        } else {
            // party dueling player (illegal)
            sender.sendMessage(ChatColor.RED + "You must leave your party to duel " + target.getPlayer().getName() + ".");
        }
    }

    public void duel(Player sender, OnlinePlayer target, KitType kitType, String arenaName) {
        if (!Validation.canSendDuel(sender, target.getPlayer())) {
            return;
        }

        DuelHandler duelHandler = Practice.getInstance().getDuelHandler();
        DuelInvite autoAcceptInvite = duelHandler.findInvite(target.getPlayer(), sender);

        // if two players duel each other for the same thing automatically
        // accept it to make their life a bit easier.
        if (autoAcceptInvite != null && autoAcceptInvite.getKitType() == kitType) {
            new AcceptCommand().accept(sender, target);
            return;
        }

        DuelInvite alreadySentInvite = duelHandler.findInvite(sender.getPlayer(), target.getPlayer());

        if (alreadySentInvite != null) {
            if (alreadySentInvite.getKitType() == kitType) {
                sender.sendMessage(ChatColor.YELLOW + "You have already invited " + ChatColor.AQUA + target.getPlayer().getName() + ChatColor.YELLOW + " to a " + kitType.getColoredDisplayName() + ChatColor.YELLOW + " duel.");
                return;
            } else {
                // if an invite was already sent (with a different kit type)
                // just delete it (so /accept will accept the 'latest' invite)
                duelHandler.removeInvite(alreadySentInvite);
            }
        }

        target.getPlayer().sendMessage(ChatColor.AQUA + sender.getName() + ChatColor.YELLOW + " has sent you a " + kitType.getColoredDisplayName() + ChatColor.YELLOW + " duel on arena " + ChatColor.LIGHT_PURPLE + arenaName + ChatColor.YELLOW + ".");
        target.getPlayer().spigot().sendMessage(createInviteNotification(sender.getName()));

        sender.sendMessage(ChatColor.YELLOW + "Successfully sent a " + kitType.getColoredDisplayName() + ChatColor.YELLOW + " duel invite to " + ChatColor.AQUA + target.getPlayer().getName() + ChatColor.YELLOW + " on arena " + ChatColor.LIGHT_PURPLE + arenaName + ChatColor.YELLOW + ".");
        duelHandler.insertInvite(new PlayerDuelInvite(sender, target.getPlayer(), kitType, arenaName));
    }

    public void duel(Player sender, Party senderParty, Party targetParty, KitType kitType, String arenaName) {
        if (!Validation.canSendDuel(senderParty, targetParty, sender)) {
            return;
        }

        DuelHandler duelHandler = Practice.getInstance().getDuelHandler();
        DuelInvite autoAcceptInvite = duelHandler.findInvite(targetParty, senderParty);
        String targetPartyLeader = Practice.getInstance().getUuidCache().name(targetParty.getLeader());

        // Automatically accept if both parties send the same duel invite
        if (autoAcceptInvite != null && autoAcceptInvite.getKitType() == kitType) {
            new AcceptCommand().accept(sender, (OnlinePlayer) Bukkit.getPlayer(targetParty.getLeader()));
            return;
        }

        DuelInvite alreadySentInvite = duelHandler.findInvite(senderParty, targetParty);

        if (alreadySentInvite != null) {
            if (alreadySentInvite.getKitType() == kitType) {
                sender.sendMessage(ChatColor.YELLOW + "You have already invited " + ChatColor.AQUA + targetPartyLeader + "'s party" + ChatColor.YELLOW + " to a " + kitType.getColoredDisplayName() + ChatColor.YELLOW + " duel.");
                return;
            } else {
                // Remove previous invite with a different kit type
                duelHandler.removeInvite(alreadySentInvite);
            }
        }

        // Notify the target party of the duel invite
        targetParty.message(ChatColor.AQUA + sender.getName() + "'s Party (" + senderParty.getMembers().size() + ")" + ChatColor.YELLOW + " has sent you a " + kitType.getColoredDisplayName() + ChatColor.YELLOW + " duel on arena " + ChatColor.LIGHT_PURPLE + arenaName + ChatColor.YELLOW + ".");
        Bukkit.getPlayer(targetParty.getLeader()).spigot().sendMessage(createInviteNotification(sender.getName()));

        // Notify the sender of the successful invite
        sender.sendMessage(ChatColor.YELLOW + "Successfully sent a " + kitType.getColoredDisplayName() + ChatColor.YELLOW + " duel invite to " + ChatColor.AQUA + targetPartyLeader + "'s party" + ChatColor.YELLOW + " on arena " + ChatColor.LIGHT_PURPLE + arenaName + ChatColor.YELLOW + ".");

        // Insert the duel invite with the specified arena
        duelHandler.insertInvite(new PartyDuelInvite(senderParty, targetParty, kitType, arenaName));
    }

    private TextComponent[] createInviteNotification(String sender) {
        TextComponent firstPart = new TextComponent("Click here or type ");
        TextComponent commandPart = new TextComponent("/accept " + sender);
        TextComponent secondPart = new TextComponent(" to accept the invite");

        firstPart.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        commandPart.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        secondPart.setColor(net.md_5.bungee.api.ChatColor.GREEN);

        ClickEvent.Action runCommand = ClickEvent.Action.RUN_COMMAND;
        HoverEvent.Action showText = HoverEvent.Action.SHOW_TEXT;

        firstPart.setClickEvent(new ClickEvent(runCommand, "/accept " + sender));
        firstPart.setHoverEvent(new HoverEvent(showText, new BaseComponent[] { new TextComponent(ChatColor.GREEN + "Click here to accept") }));

        commandPart.setClickEvent(new ClickEvent(runCommand, "/accept " + sender));
        commandPart.setHoverEvent(new HoverEvent(showText, new BaseComponent[] { new TextComponent(ChatColor.GREEN + "Click here to accept") }));

        secondPart.setClickEvent(new ClickEvent(runCommand, "/accept " + sender));
        secondPart.setHoverEvent(new HoverEvent(showText, new BaseComponent[] { new TextComponent(ChatColor.GREEN + "Click here to accept") }));

        return new TextComponent[] { firstPart, commandPart, secondPart };
    }

}