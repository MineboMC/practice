package net.minebo.practice.command.event;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import net.minebo.practice.Practice;
import net.minebo.practice.events.menu.EventKitSelectMenu;
import net.minebo.practice.events.menu.EventSelectMenu;
import net.minebo.practice.party.PartyHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HostCommand extends BaseCommand {

    @CommandAlias("host")
    @Description("Host an event.")
    public static void hostCommand(Player sender) {
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        if (!Practice.getInstance().getLobbyHandler().isInLobby(sender)) {
            sender.sendMessage(ChatColor.RED + "You must be in the lobby to host an event.");
            return;
        }

        if (partyHandler.hasParty(sender)) {
            sender.sendMessage(ChatColor.RED + "You cannot host an event while in a party.");
            return;
        }
        new EventSelectMenu(eventType -> {
            if (!Practice.getInstance().getEventHandler().hasPermissionToHost(sender, eventType)) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to host " + eventType.getName() + " events.");
                return;
            }
            sender.closeInventory();
            if (eventType.isAllowKitSelection()) {
                new EventKitSelectMenu(kitType -> {
                    sender.closeInventory();
                    sender.sendMessage(ChatColor.GREEN + "Trying to start event...");
                    Practice.getInstance().getEventHandler().hostEvent(sender, eventType, kitType);
                }, "Select kit for event", eventType).openMenu(sender);
            } else {
                sender.sendMessage(ChatColor.GREEN + "Trying to start event...");
                Practice.getInstance().getEventHandler().hostEvent(sender, eventType, null);
            }
        }, "Select an event").openMenu(sender);
    }
}
