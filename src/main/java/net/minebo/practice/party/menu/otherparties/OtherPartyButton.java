package net.minebo.practice.party.menu.otherparties;

import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.google.common.base.Preconditions;

import net.minebo.practice.Practice;
import net.minebo.practice.command.duel.DuelCommand;
import net.minebo.practice.party.Party;
import net.minebo.practice.util.menu.Button;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

final class OtherPartyButton extends Button {

    private final Party party;

    OtherPartyButton(Party party) {
        this.party = Preconditions.checkNotNull(party, "party");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.DARK_PURPLE + Practice.getInstance().getUuidCache().name(party.getLeader());
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = new ArrayList<>();

        description.add("");

        for (UUID member : party.getMembers()) {
            ChatColor color = party.isLeader(member) ? ChatColor.DARK_PURPLE : ChatColor.YELLOW;
            description.add(color + Practice.getInstance().getUuidCache().name(member));
        }

        description.add("");
        description.add(ChatColor.GREEN + "» Click to duel «");

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.SKULL_ITEM;
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte) 3; // player head
    }

    @Override
    public int getAmount(Player player) {
        return party.getMembers().size();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        Party senderParty = Practice.getInstance().getPartyHandler().getParty(player);

        if (senderParty == null) {
            return;
        }

        if (senderParty.isLeader(player.getUniqueId())) {
            new DuelCommand().duel(player, (OnlinePlayer) Bukkit.getPlayer(party.getLeader()));
        } else {
            player.sendMessage(ChatColor.RED + "Only the leader can duel other parties!");
        }
    }

}