package net.minebo.practice.events;

import net.minebo.practice.events.enums.EventPlayerState;
import net.minebo.practice.events.enums.EventState;
import net.minebo.practice.events.enums.EventType;
import lombok.Getter;
import lombok.Setter;
import net.minebo.practice.kit.kittype.KitType;

import java.util.*;

public class Event {

    @Getter public EventType type;
    @Getter public EventState state;
    public static Set<UUID> activePlayers;
    public static Set<UUID> spectators;
    public HashMap<UUID, EventPlayerState> playerStates;
    @Setter @Getter
    private KitType kit;

    public Event(EventType type) {
        this.type = type;
        this.state = EventState.WAITING;
        this.activePlayers = new HashSet<>();
        this.spectators = new HashSet<>();
        this.playerStates = new HashMap<>();
    }

    public boolean isPlayerInEvent(UUID player) {
        return activePlayers.contains(player);
    }

    public void addPlayer(UUID player) {
        activePlayers.add(player);
    }

    public void removePlayer(UUID player) {
        if (activePlayers.contains(player)) {
            activePlayers.remove(player);
        }
        if (spectators.contains(player)) {
            spectators.remove(player);
        }
    }

    public void setState(EventState state) {
        this.state = state;
    }

    public Set<UUID> getSpectatorsAndPlayers() {
        Set<UUID> players = new HashSet<>();
        players.addAll(activePlayers);
        players.addAll(spectators);
        return players;
    }
}
