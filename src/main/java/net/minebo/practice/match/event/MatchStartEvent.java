package net.minebo.practice.match.event;

import net.minebo.practice.match.Match;

import net.minebo.practice.match.MatchState;
import org.bukkit.event.HandlerList;

import lombok.Getter;

/**
 * Called when a match's countdown ends (when its {@link MatchState} changes
 * to {@link MatchState#IN_PROGRESS})
 * @see MatchState#IN_PROGRESS
 */
public final class MatchStartEvent extends MatchEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    public MatchStartEvent(Match match) {
        super(match);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}