package net.minebo.practice.party.event;

import net.minebo.practice.party.Party;

import net.minebo.practice.party.PartyHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import lombok.Getter;

/**
 * Called when a {@link Party} is created.
 * @see net.frozenorb.potpvp.party.command.PartyCreateCommand
 * @see PartyHandler#getOrCreateParty(Player)
 */
public final class PartyCreateEvent extends PartyEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    public PartyCreateEvent(Party party) {
        super(party);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}