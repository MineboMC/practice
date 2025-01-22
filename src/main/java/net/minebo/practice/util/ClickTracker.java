package net.minebo.practice.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.minebo.practice.Practice;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class ClickTracker implements Listener {

    private static Map<Player, Integer> clicks = new HashMap<>();
    private static Map<UUID, Integer> playerClickTooFastBitch = new HashMap<>();

    public static void init() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Practice.getInstance(), PacketType.Play.Client.ARM_ANIMATION) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                try {
                    if (event.getPlayer().getTargetBlock((HashSet<Byte>) null, 4).getType() == Material.AIR) {
                        Player player = event.getPlayer();

                        clicks.merge(player, 1, Integer::sum);
                        if (clicks.get(player) > 22) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (clicks.get(player) > 22 && !player.hasPermission("potpvp.cpsbypass")) {
                                        playerClickTooFastBitch.merge(player.getUniqueId(), 1, Integer::sum);
                                        if (playerClickTooFastBitch.get(player.getUniqueId()) >= 3) {
                                            playerClickTooFastBitch.remove(player.getUniqueId());
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tempban " + player.getName() + " 1d You were banned for clicking too fast after multiple warnings. Please download DC Prevent, turn up your debounce time, or use a different clicking method.");
                                        } else {
                                            player.kickPlayer(ChatColor.RED + ("You were kicked for clicking too fast! Please do not butterfly or drag click to avoid future punishments. " + "\n\nThis type of punishment cannot be appealed."));
                                        }
                                    }
                                }
                            }.runTaskLater(Practice.getInstance(), 60L);
                        }
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                clicks.merge(player, -1, Integer::sum);
                            }
                        }.runTaskLater(Practice.getInstance(), 20);
                    }
                } catch (Exception e) {
                }

            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        clicks.put(event.getPlayer(), 0);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        clicks.remove(event.getPlayer());
    }

    public static int getClicks(Player p) {
        return clicks.getOrDefault(p, 0);
    }
}