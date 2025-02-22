package net.minebo.practice.match.listener;

import net.minebo.practice.Practice;
import net.minebo.practice.kit.Kit;
import net.minebo.practice.kit.KitHandler;
import net.minebo.practice.kit.kittype.KitType;
import net.minebo.practice.match.Match;
import net.minebo.practice.match.MatchHandler;
import net.minebo.practice.match.MatchTeam;
import net.minebo.practice.match.event.MatchCountdownStartEvent;

import net.minebo.practice.party.Party;
import net.minebo.practice.pvpclasses.PvPClasses;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class KitSelectionListener implements Listener {

    /**
     * Give players their kits when their match countdown starts
     */
    @EventHandler
    public void onMatchCountdownStart(MatchCountdownStartEvent event) {
        KitHandler kitHandler = Practice.getInstance().getKitHandler();
        Match match = event.getMatch();
        KitType kitType = match.getKitType();

        if (kitType.getId().equals("SUMO")) return; // no kits for sumo

        for (Player player : Bukkit.getOnlinePlayers()) {
            MatchTeam team = match.getTeam(player.getUniqueId());

            if (team == null) {
                continue;
            }

            List<Kit> customKits = kitHandler.getKits(player, kitType);
            ItemStack defaultKitItem = Kit.ofDefaultKit(kitType).createSelectionItem();

            if (kitType.equals(KitType.teamFight)) {

                KitType bard;
                KitType diamond;
                KitType archer;
                KitType rogue;

                if(Practice.getInstance().getArenaHandler().getSchematic(event.getMatch().getArena().getSchematic()).isCitadelMap()){
                    bard = KitType.byId("BARD_CITADEL");
                    diamond = KitType.byId("DIAMOND_CITADEL");
                    archer = KitType.byId("ARCHER_CITADEL");
                    rogue = KitType.byId("ROGUE_CITADEL");
                } else {
                    bard = KitType.byId("BARD_HCF");
                    diamond = KitType.byId("DIAMOND_HCF");
                    archer = KitType.byId("ARCHER_HCF");
                    rogue = KitType.byId("ROGUE_HCF");
                }

                Party party = Practice.getInstance().getPartyHandler().getParty(player);

                if (party == null) {
                    Kit.ofDefaultKit(diamond).apply(player);
                } else {
                    PvPClasses kit = party.getKits().getOrDefault(player.getUniqueId(), PvPClasses.DIAMOND);

                    if (kit == null || kit == PvPClasses.DIAMOND) {
                        Kit.ofDefaultKit(diamond).apply(player);
                    } else if (kit == PvPClasses.BARD) {
                        Kit.ofDefaultKit(bard).apply(player);
                    } else if (kit == PvPClasses.ROGUE) {
                        Kit.ofDefaultKit(rogue).apply(player);
                    } else {
                        Kit.ofDefaultKit(archer).apply(player);
                    }

                }

            } else {
                // if they have no kits saved place default in 0, otherwise
                // the default goes in 9 and they get custom kits from 1-4
                if (customKits.isEmpty()) {
                    player.getInventory().setItem(0, defaultKitItem);
                } else {
                    for (Kit customKit : customKits) {
                        // subtract one to convert from 1-indexed kts to 0-indexed inventories
                        player.getInventory().setItem(customKit.getSlot() - 1, customKit.createSelectionItem());
                    }

                    player.getInventory().setItem(8, defaultKitItem);
                }
            }


            player.updateInventory();
        }
    }

    /**
     * Don't let players drop their kit selection books via the Q key
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(event.getPlayer());

        if (match == null) {
            return;
        }

        KitHandler kitHandler = Practice.getInstance().getKitHandler();
        ItemStack droppedItem = event.getItemDrop().getItemStack();
        KitType kitType = match.getKitType();

        for (Kit kit : kitHandler.getKits(event.getPlayer(), kitType)) {
            if (kit.isSelectionItem(droppedItem)) {
                event.setCancelled(true);
                return;
            }
        }

        Kit defaultKit = Kit.ofDefaultKit(kitType);

        if (defaultKit.isSelectionItem(droppedItem)) {
            event.setCancelled(true);
        }
    }

    /**
     * Don't let players drop their kit selection items via death
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(event.getEntity());

        if (match == null) {
            return;
        }

        KitHandler kitHandler = Practice.getInstance().getKitHandler();
        KitType kitType = match.getKitType();

        for (Kit kit : kitHandler.getKits(event.getEntity(), kitType)) {
            event.getDrops().remove(kit.createSelectionItem());
        }

        event.getDrops().remove(Kit.ofDefaultKit(kitType).createSelectionItem());
    }

    /**
     * Give players their kits upon right click
     */
    // no ignoreCancelled = true because right click on air
    // events are by default cancelled (wtf Bukkit)
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || !event.getAction().name().contains("RIGHT_")) {
            return;
        }

        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(event.getPlayer());

        if (match == null) {
            return;
        }

        KitHandler kitHandler = Practice.getInstance().getKitHandler();
        ItemStack clickedItem = event.getItem();
        KitType kitType = match.getKitType();
        Player player = event.getPlayer();

        for (Kit kit : kitHandler.getKits(player, kitType)) {
            if (kit.isSelectionItem(clickedItem)) {
                kit.apply(player);
                player.sendMessage(ChatColor.YELLOW + "You equipped your \"" + kit.getName() + "\" " + kitType.getDisplayName() + " kit.");
                return;
            }
        }

        Kit defaultKit = Kit.ofDefaultKit(kitType);

        if (defaultKit.isSelectionItem(clickedItem)) {
            defaultKit.apply(player);
            player.sendMessage(ChatColor.YELLOW + "You equipped the default kit for " + kitType.getDisplayName() + ".");
        }

    }

}