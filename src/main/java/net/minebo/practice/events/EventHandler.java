package net.minebo.practice.events;

import mkremins.fanciful.FancyMessage;
import net.minebo.practice.Practice;
import net.minebo.practice.misc.Lang;
import net.minebo.practice.arena.Arena;
import net.minebo.practice.arena.ArenaHandler;
import net.minebo.practice.events.enums.EventPlayerState;
import net.minebo.practice.events.enums.EventState;
import net.minebo.practice.events.enums.EventType;
import net.minebo.practice.events.games.deathrace.DeathRaceHandler;
import net.minebo.practice.events.games.deathrace.DeathRaceListener;
import net.minebo.practice.events.games.lms.LMSHandler;
import net.minebo.practice.events.games.lms.LMSListener;
import net.minebo.practice.events.games.oitc.OITCHandler;
import net.minebo.practice.events.games.oitc.OITCListener;
import net.minebo.practice.events.games.sumo.SumoHandler;
import net.minebo.practice.events.games.sumo.SumoListener;
import net.minebo.practice.kit.kittype.KitType;
import net.minebo.practice.util.InventoryUtils;
import net.minebo.practice.util.PatchedPlayerUtils;
import net.minebo.practice.util.VisibilityUtils;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class EventHandler {
    static ArenaHandler arenaHandler = Practice.getInstance().getArenaHandler();

    @Getter
    private static Event currentEvent = null;
    @Getter
    public static Arena arena = null;
    ItemStack leaveItem = new ItemStack(Material.INK_SACK, 1, (byte) 1);
    public static int[] countdown = { 60 };
    public boolean forceStart = false;

    public EventHandler() {
        Practice.getInstance().getServer().getPluginManager().registerEvents(new EventListener(), Practice.getInstance());

        Practice.getInstance().getServer().getPluginManager().registerEvents(new SumoListener(), Practice.getInstance());
        Practice.getInstance().getServer().getPluginManager().registerEvents(new LMSListener(), Practice.getInstance());
        Practice.getInstance().getServer().getPluginManager().registerEvents(new DeathRaceListener(), Practice.getInstance());
        Practice.getInstance().getServer().getPluginManager().registerEvents(new OITCListener(), Practice.getInstance());
    }

    public void hostEvent(Player hostPlayer, EventType eventType, KitType kitType) {
        if (currentEvent != null) {
            hostPlayer.sendMessage("§cThere is already an event running!");
            return;
        }
        Optional<Arena> openArenaOpt;
        currentEvent = new Event(eventType);

        openArenaOpt = arenaHandler.allocateUnusedArena(schematic -> {
            if(schematic.isEnabled()){
                switch(eventType){
                    case LMS: {
                        return schematic.getSupportsLMS();
                    }
                    case OITC: {
                        return schematic.getSupportsOITC();
                    }
                    case DEATHRACE: {
                        return schematic.getSupportsDeathRace();
                    }
                    case SUMO: {
                        return schematic.getSupportsSumo();
                    }
                }
            }
            return false;
        });

        if (!openArenaOpt.isPresent()) {
            hostPlayer.sendMessage("§cThere are no open arenas for this event!");
            Practice.getInstance().getLogger().warning("Failed to initialize event: No open arenas found");
            currentEvent.setState(EventState.INACTIVE);
            currentEvent = null;
            return;
        }

        EventHandler.arena = openArenaOpt.get();
        if (kitType != null) {
            currentEvent.setKit(kitType);
        }

        currentEvent.setState(EventState.WAITING);


        String finalRankColor = PatchedPlayerUtils.getFormattedName(hostPlayer.getUniqueId());
        new BukkitRunnable() {
            @Override
            public void run() {
                if (countdown[0] <= 0 || forceStart) {
                    String message = ChatColor.GRAY + "has started.";
                    boolean cancelEvent = Event.activePlayers.size() <= 1;
                    if (cancelEvent) {
                        message = ChatColor.GRAY + "has been cancelled.";
                    }
                    for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                        String[] hostMessage = {
                                ChatColor.GRAY + "The event " + ChatColor.YELLOW + eventType.getName() + ChatColor.GRAY + " " + (forceStart ? "has started by an administrator." : message) + ChatColor.WHITE + " (" + Event.activePlayers.size() + "/" + eventType.getMaxPlayers() + ")",
                        };
                        p.sendMessage(hostMessage);
                    }
                    if (cancelEvent) {
                        endEvent(null);
                        forceStart = false;
                        countdown[0] = 60;
                        this.cancel();
                    } else {
                        startEvent();
                        forceStart = false;
                        countdown[0] = 60;
                        this.cancel();
                    }
                    this.cancel();
                    return;
                }

                if (countdown[0] % 15 == 0) {
                    for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                        FancyMessage message = new FancyMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', ChatColor.GRAY + "Click " + ChatColor.GOLD + "here " + ChatColor.GRAY + "or type " + ChatColor.GOLD + "&l/event join" + ChatColor.GRAY + ". (" + Event.activePlayers.size() + "/" + eventType.getMaxPlayers() + ")")).command("/event join").tooltip(org.bukkit.ChatColor.GREEN + "Click to join!");
                        p.sendMessage(hostPlayer.getDisplayName() + ChatColor.GRAY + " is hosting the "+ ChatColor.YELLOW + eventType.getName() + ChatColor.GRAY + " event!");
                        message.send(p);
                    }
                }

                countdown[0]--;
            }
        }.runTaskTimer(Practice.getInstance(), 20L, 20L);
    }

    public void addPlayerToEvent(Player player) {
        if (currentEvent != null && currentEvent.state != EventState.WAITING) {
            return;
        }
        if (Event.activePlayers.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are already in the event.");
            return;
        }
        currentEvent.addPlayer(player.getUniqueId());
        addToWaiting(player);
        player.sendMessage(ChatColor.GREEN + "You have joined the " + currentEvent.getType().getName() + " event.");

        if (currentEvent.state == EventState.WAITING) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', player.getDisplayName() + ChatColor.GRAY + " joined the " + ChatColor.GOLD + currentEvent.getType().getName() + ChatColor.GRAY + " event. " + ChatColor.YELLOW + "("+ Event.activePlayers.size() + "/" + currentEvent.getType().getMaxPlayers() + ")"));
        }
    }

    public void removePlayerFromEvent(Player player) {
        if (currentEvent == null) {
            return;
        }
        currentEvent.removePlayer(player.getUniqueId());
        currentEvent.playerStates.remove(player.getUniqueId());
        if (player.isOnline()) {
            Practice.getInstance().getLobbyHandler().returnToLobby(player);
            VisibilityUtils.updateVisibility(player);
        }
        if (currentEvent.state == EventState.WAITING) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', player.getDisplayName() + ChatColor.GRAY + " has left the " + ChatColor.YELLOW + currentEvent.getType().getName() + ChatColor.GRAY + " event. " + ChatColor.YELLOW + "(" + Event.activePlayers.size() + "/" + currentEvent.getType().getMaxPlayers() + ")"));
        }
        if (Event.activePlayers.size() == 1 && currentEvent.state == EventState.STARTED) {
            Player lastPlayer = Bukkit.getPlayer(Event.activePlayers.stream().findFirst().get());
            endEvent(lastPlayer);
        }
    }

    public void startEvent() {
        if (currentEvent == null) return;
        if (Event.activePlayers.size() < currentEvent.getType().getMinPlayers()) {
            for (UUID uuid : currentEvent.getSpectatorsAndPlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    player.sendMessage("§cNot enough players to start the event!");
                    Practice.getInstance().getLobbyHandler().returnToLobby(player);
                    endEvent(null);
                }
            }
            currentEvent = null;
            return;
        }
        currentEvent.setState(EventState.STARTED);
        if (currentEvent.getType() == EventType.SUMO) {
            SumoHandler.startSumoEvent(currentEvent);
        } else if (currentEvent.getType() == EventType.LMS) {
            LMSHandler.startLMSEvent(currentEvent);
        } else if (currentEvent.getType() == EventType.DEATHRACE) {
            DeathRaceHandler.startDeathRaceEvent(currentEvent);
        } else if (currentEvent.getType() == EventType.OITC) {
            OITCHandler.startOITCEvent(currentEvent);
        }
    }

    public static void endEvent(Player winner) {
        if (currentEvent == null) return;
        currentEvent.setState(EventState.INACTIVE);
        for (UUID uuid : currentEvent.getSpectatorsAndPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.removePotionEffect(PotionEffectType.BLINDNESS);
                Practice.getInstance().getLobbyHandler().returnToLobby(player);
                currentEvent.removePlayer(uuid);
                PatchedPlayerUtils.allowMovement(player);
                currentEvent.playerStates.remove(uuid);
                VisibilityUtils.updateVisibility(player);
            }
        }

        arena.restore();

        if (currentEvent.getType() == EventType.SUMO) {
            SumoHandler.round = 0;
        }
        arenaHandler.releaseArena(arena);
        if (winner != null) {
            int[] outputTimes = { 3 };
            String eventName = currentEvent.getType().getName();

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (outputTimes[0] <= 0) {
                        this.cancel();
                        return;
                    }
                    if (winner.getName() != null) {
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', winner.getDisplayName() + ChatColor.GRAY + " has won the " + ChatColor.GOLD + eventName + ChatColor.GRAY + " event!"));
                    }
                    outputTimes[0]--;
                }
            }.runTaskTimer(Practice.getInstance(), 40L, 40L);
        }
        currentEvent = null;
    }

    public void addToWaiting(Player player) {
        Event event = getCurrentEvent();
        if (event == null) {
            return;
        }

        event.playerStates.put(player.getUniqueId(), EventPlayerState.WAITING);

        VisibilityUtils.updateVisibility(player);
        PatchedPlayerUtils.resetInventory(player, GameMode.SURVIVAL, true);
        InventoryUtils.resetInventoryDelayed(player);

        if (event.type.isTeleportOnJoin()) {
            player.teleport(arena.getSpectatorSpawn());
        } else {
            player.teleport(new Location(Bukkit.getWorld("world"), 45000.5, 50, 45000.5, 0, 0));
        }

        player.getInventory().setItem(8, EventItems.getLeaveItem());
    }

    public void addToSpectating(Player player) {
        Event event = getCurrentEvent();
        if (event == null) {
            return;
        }

        if (!event.type.isTeleportOnJoin() && event.state == EventState.WAITING) {
            player.sendMessage(ChatColor.RED + "The event hasn't started yet.");
            return;
        }

        Event.spectators.add(player.getUniqueId());

        event.playerStates.put(player.getUniqueId(), EventPlayerState.SPECTATING);

        VisibilityUtils.updateVisibility(player);
        PatchedPlayerUtils.resetInventory(player, GameMode.SURVIVAL, true);
        InventoryUtils.resetInventoryDelayed(player);
        player.sendMessage(org.bukkit.ChatColor.RED + "You are now spectating the event.");
        player.removePotionEffect(PotionEffectType.BLINDNESS);

        PatchedPlayerUtils.allowMovement(player);

        player.setAllowFlight(true);
        player.setFlying(true);

        player.getInventory().setItem(8, EventItems.getLeaveItem());
        player.teleport(arena.getSpectatorSpawn());
    }

    public boolean hasPermissionToHost(Player player, EventType eventType) {
        return player.hasPermission("practice.host.all") || player.hasPermission(eventType.getPermission());
    }

    public static void messageAll(String message) {
        Set<UUID> allPlayers = new HashSet<>();
        allPlayers.addAll(Event.activePlayers);
        allPlayers.addAll(Event.spectators);

        for (UUID uuid : allPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes(
                        '&', message));
            }
        }
    }
}
