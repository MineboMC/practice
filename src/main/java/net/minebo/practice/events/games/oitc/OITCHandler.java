package net.minebo.practice.events.games.oitc;

import net.minebo.practice.Practice;
import net.minebo.practice.events.Event;
import net.minebo.practice.events.EventHandler;
import net.minebo.practice.events.enums.EventPlayerState;
import net.minebo.practice.events.enums.EventState;
import net.minebo.practice.util.InventoryUtils;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import net.minebo.practice.util.PatchedPlayerUtils;
import lombok.Getter;
import lombok.Setter;
import net.minebo.practice.util.PlayerUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class OITCHandler {
    public static int killsToWin = 20;

    @Getter @Setter static HashMap<Player, Integer> kills = new HashMap<>();



    public static void startOITCEvent(Event event) {
        if (event.state == EventState.INACTIVE) {
            return;
        }

        List<Location> eventSpawns = EventHandler.getArena().getEventSpawns();
        int eventSpawnId = 0;

        if (event.state == EventState.STARTED) {
            for (UUID uuid : Event.activePlayers) {
                Player player = Bukkit.getPlayer(uuid);

                if (event.playerStates.get(uuid) == EventPlayerState.WAITING) {
                    event.playerStates.put(uuid, EventPlayerState.FIGHTING);
                    kills.put(player, 0);

                    giveOITCkit(player);

                    player.setAllowFlight(true);


                    if (eventSpawns.get(eventSpawnId) == null)
                        eventSpawnId = 0;

                    player.teleport(eventSpawns.get(eventSpawnId));
                    eventSpawnId++;
                }
            }
        }
    }

    public static void giveOITCkit(Player player) {
        InventoryUtils.resetInventoryNow(player);
        Inventory inventory = player.getInventory();

        inventory.setItem(0, new ItemStack(Material.BOW, 1));
        inventory.setItem(1, new ItemStack(Material.WOOD_PICKAXE, 1));
        inventory.setItem(2, new ItemStack(Material.ARROW, 1));

        player.setHealth(20);

        player.setLevel(0);

        OITCListener.canSlam.remove(player);
    }

    public static void killTheFuckingPlayer(Player dead, Player killer) {
        if (EventHandler.getCurrentEvent() == null) return;
        Event event = EventHandler.getCurrentEvent();
        if (event.playerStates.containsKey(dead.getUniqueId()) && event.playerStates.get(dead.getUniqueId()) != EventPlayerState.FIGHTING) return;

        float thunderSoundPitch = 0.8F + ThreadLocalRandom.current().nextFloat() * 0.2F;
        float explodeSoundPitch = 0.5F + ThreadLocalRandom.current().nextFloat() * 0.2F;

        PlayerUtils.animateDeath(dead, true);
        for (UUID uuid : event.getSpectatorsAndPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.playSound(dead.getLocation(), Sound.AMBIENCE_THUNDER, 10000F, thunderSoundPitch);
                player.playSound(dead.getLocation(), Sound.EXPLODE, 2.0F, explodeSoundPitch);

                PacketContainer lightningPacket = createLightningPacket(dead.getLocation());
                sendLightningPacket(player, lightningPacket);
            }
        }
        dead.setHealth(20);

        if (killer != null) {
            kills.merge(killer, 1, Integer::sum);
            killer.getInventory().addItem(new ItemStack(Material.ARROW, 1));

            if (kills.get(killer) == killsToWin - 1) {
                EventHandler.messageAll("&4&lMatch Point! &7- " + PatchedPlayerUtils.getFormattedName(killer.getUniqueId()) + "&e needs &61&e more kill to win!");
            }
        }

        List<Location> eventSpawns = EventHandler.getArena().getEventSpawns();
        Location randomSpawn = eventSpawns.get((int) (Math.random() * eventSpawns.size()));


        dead.setVelocity(new Vector(0, 0, 0));
        dead.teleport(randomSpawn);

        new BukkitRunnable() {
            @Override
            public void run() {
                giveOITCkit(dead);
            }
        }.runTaskLater(Practice.getInstance(), 1L);
    }

    public static int getKills(Player player) {
        return kills.getOrDefault(player, 0);
    }

    private static PacketContainer createLightningPacket(Location location) {
        PacketContainer lightningPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_WEATHER);

        lightningPacket.getModifier().writeDefaults();
        lightningPacket.getIntegers().write(0, 128); // entity id of 128
        lightningPacket.getIntegers().write(4, 1); // type of lightning (1)
        lightningPacket.getIntegers().write(1, (int) (location.getX() * 32.0D)); // x
        lightningPacket.getIntegers().write(2, (int) (location.getY() * 32.0D)); // y
        lightningPacket.getIntegers().write(3, (int) (location.getZ() * 32.0D)); // z

        return lightningPacket;
    }

    private static void sendLightningPacket(Player target, PacketContainer packet) {
        ProtocolLibrary.getProtocolManager().sendServerPacket(target, packet);
    }
}
