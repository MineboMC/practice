package net.minebo.practice.match.duel;

import net.minebo.practice.kit.kittype.KitType;
import net.minebo.practice.party.Party;

public final class PartyDuelInvite extends DuelInvite<Party> {

    public PartyDuelInvite(Party sender, Party target, KitType kitTypes) {
        super(sender, target, kitTypes);
    }

}