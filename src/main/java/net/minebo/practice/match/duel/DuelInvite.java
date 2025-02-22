package net.minebo.practice.match.duel;

import com.google.common.base.Preconditions;

import net.minebo.practice.kit.Kit;
import net.minebo.practice.kit.kittype.KitType;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import lombok.Getter;

public abstract class DuelInvite<T> {

    @Getter private final T sender;
    @Getter private final T target;
    @Getter private final KitType kitType;
    @Getter private final String arenaName;
    @Getter private final Instant timeSent;

    public DuelInvite(T sender, T target, KitType kitType, String arenaName) {
        this.sender = Preconditions.checkNotNull(sender, "sender");
        this.target = Preconditions.checkNotNull(target, "target");
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
        this.arenaName = arenaName; // Arena can be null -> random arena
        this.timeSent = Instant.now();
    }

    public boolean isExpired() {
        long sentAgo = ChronoUnit.SECONDS.between(timeSent, Instant.now());
        return sentAgo > DuelHandler.DUEL_INVITE_TIMEOUT_SECONDS;
    }

}