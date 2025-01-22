package net.minebo.practice.command.event;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import net.minebo.practice.Practice;
import net.minebo.practice.events.Event;
import net.minebo.practice.events.EventHandler;
import net.minebo.practice.events.enums.EventState;
import net.minebo.practice.party.PartyHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


@CommandAlias("event")
public class EventCommands extends BaseCommand {

    @Subcommand("leave")
    @Description("Leave an event.")
    public static void leaveEvent(Player sender) {
        EventHandler eventManager = Practice.getInstance().getEventHandler();
        Event currentEvent = EventHandler.getCurrentEvent();
        if (currentEvent == null) {
            sender.sendMessage(ChatColor.RED + "There is no event running!");
            return;
        }
        if (currentEvent.getSpectatorsAndPlayers().contains(sender.getUniqueId())) {
            eventManager.removePlayerFromEvent(sender);
            sender.sendMessage(ChatColor.RED + "You have left the event!");
        } else {
            sender.sendMessage(ChatColor.RED + "You are not in the event!");
        }
    }

    @Subcommand("spectate")
    @Description("Spectate an event .")
    public static void specEvent(Player sender) {
        EventHandler eventManager = Practice.getInstance().getEventHandler();
        Event currentEvent = EventHandler.getCurrentEvent();
        if (currentEvent != null) {
            if (currentEvent.getSpectatorsAndPlayers().contains(sender.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "You are already in this event.");
                return;
            }
            eventManager.addToSpectating(sender);
        } else {
            sender.sendMessage(ChatColor.RED + "There is no event running!");
        }
    }

    @Subcommand("forcestart")
    @Description("Force start an event.")
    @CommandPermission("potpvp.admin")
    public static void forcestartEvent(Player sender) {
        Event currentEvent = EventHandler.getCurrentEvent();
        if (currentEvent == null) {
            sender.sendMessage(ChatColor.RED + "There is no event running!");
            return;
        }
        if (currentEvent.state != EventState.WAITING) {
            sender.sendMessage(ChatColor.RED + "The event is already running!");
            return;
        }
        EventHandler.countdown[0] = 1;
    }

    @Subcommand("join")
    @Description("Join an active event.")
    public static void join(Player sender) {
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        EventHandler eventManager = Practice.getInstance().getEventHandler();
        Event event = EventHandler.getCurrentEvent();
        if (!Practice.getInstance().getLobbyHandler().isInLobby(sender)) {
            sender.sendMessage(ChatColor.RED + "You must be in the lobby to join an event.");
            return;
        }
        if (event == null) {
            sender.sendMessage(ChatColor.RED + "There is no active event.");
            return;
        }
        if (event.getSpectatorsAndPlayers().contains(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are already in this event.");
            return;
        }
        if (partyHandler.hasParty(sender)) {
            sender.sendMessage(ChatColor.RED + "You cannot join an event while in a party.");
            return;
        }
        if (event.state != EventState.WAITING) {
            eventManager.addToSpectating(sender);
            return;
        }

        Practice.getInstance().getEventHandler().addPlayerToEvent(sender);
    }
}
