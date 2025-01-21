package net.minebo.practice.match.duel;

import java.util.UUID;

import org.bukkit.entity.Player;

import net.minebo.practice.kit.kittype.KitType;

public final class PlayerDuelInvite extends DuelInvite<UUID> {

    public PlayerDuelInvite(Player sender, Player target, KitType kitType) {
        super(sender.getUniqueId(), target.getUniqueId(), kitType);
    }

}