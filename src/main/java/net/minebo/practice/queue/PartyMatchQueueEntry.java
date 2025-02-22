package net.minebo.practice.queue;

import com.google.common.base.Preconditions;

import net.minebo.practice.party.Party;

import java.util.Set;
import java.util.UUID;

import lombok.Getter;

/**
 * Represents a {@link Party} waiting
 * in a {@link MatchQueue}
 */
public final class PartyMatchQueueEntry extends MatchQueueEntry {

    @Getter private final Party party;

    PartyMatchQueueEntry(MatchQueue queue, Party party) {
        super(queue);

        this.party = Preconditions.checkNotNull(party, "party");
    }

    @Override
    public Set<UUID> getMembers() {
        return party.getMembers();
    }

}