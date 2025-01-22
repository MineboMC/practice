package net.minebo.practice.party.listener;

import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import net.minebo.practice.Practice;
import net.minebo.practice.command.PartyCommands;
import net.minebo.practice.party.Party;
import net.minebo.practice.party.PartyHandler;
import net.minebo.practice.party.PartyItems;
import net.minebo.practice.party.menu.RosterMenu;
import net.minebo.practice.party.menu.otherparties.OtherPartiesMenu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class PartyItemListener implements Listener {

    private static final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();
    private final PartyHandler partyHandler;

    private final Map<ItemStack, Consumer<Player>> itemHandlers = new HashMap<>();

    public PartyItemListener(PartyHandler partyHandler) {
        this.partyHandler = partyHandler;

        // Initialize item handlers
        itemHandlers.put(PartyItems.LEAVE_PARTY_ITEM, new PartyCommands()::leave);
        itemHandlers.put(PartyItems.START_TEAM_SPLIT_ITEM, new PartyCommands()::teamSplit);
        itemHandlers.put(PartyItems.START_FFA_ITEM, new PartyCommands()::partyFFA);
        itemHandlers.put(PartyItems.OTHER_PARTIES_ITEM, p -> new OtherPartiesMenu().openMenu(p));
        itemHandlers.put(PartyItems.ASSIGN_CLASSES, p -> {
            Party party = partyHandler.getParty(p);
            if (party != null) {
                new RosterMenu(party).openMenu(p);
            }
        });
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Check for right-click actions
        if (!event.hasItem() || !event.getAction().name().contains("RIGHT_")) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // Handle cooldown
        if (isOnCooldown(player)) {
            return;
        }

        // Match item and execute handler
        for (Map.Entry<ItemStack, Consumer<Player>> entry : itemHandlers.entrySet()) {
            if (item.isSimilar(entry.getKey())) {
                event.setCancelled(true);
                entry.getValue().accept(player);
                addCooldown(player, 500); // Add 500ms cooldown
                return;
            }
        }

        // Special handling for dynamic party icon
        if (item.getType() == PartyItems.ICON_TYPE) {
            handlePartyIcon(player, item, event);
        }
    }

    private void handlePartyIcon(Player player, ItemStack item, PlayerInteractEvent event) {
        Party party = Practice.getInstance().getPartyHandler().getParty(player);

        if (party != null && PartyItems.icon(party).isSimilar(item)) {
            event.setCancelled(true);
            new PartyCommands().partyInfo(player, new OnlinePlayer(player));
            addCooldown(player, 500); // Add 500ms cooldown
        }
    }

    private boolean isOnCooldown(Player player) {
        return cooldowns.getOrDefault(player.getUniqueId(), 0L) > System.currentTimeMillis();
    }

    private void addCooldown(Player player, int ms) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + ms);
    }
}
