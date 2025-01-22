package net.minebo.practice.util.nametags;

import com.comphenix.protocol.ProtocolLibrary;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.minebo.practice.util.nametags.construct.NameTagComparator;
import net.minebo.practice.util.nametags.construct.NameTagInfo;
import net.minebo.practice.util.nametags.construct.NameTagUpdate;
import net.minebo.practice.util.nametags.listener.NameTagListener;
import net.minebo.practice.util.nametags.packet.ScoreboardTeamPacketMod;
import net.minebo.practice.util.nametags.provider.DefaultNameTagProvider;
import net.minebo.practice.util.nametags.provider.NameTagProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class NameTagHandler {

    @Getter private static JavaPlugin plugin;

    @Getter private static Map<String, Map<String, NameTagInfo>> teamMap = new ConcurrentHashMap<>();
    @Getter private static List<NameTagInfo> registeredTeams = Collections.synchronizedList(new ArrayList<>());
    @Getter private static int teamCreateIndex = 1;
    @Getter private static List<NameTagProvider> providers = new ArrayList<>();
    @Getter private boolean nametagRestrictionEnabled = false;
    @Getter private String nametagRestrictBypass = "";
    @Getter @Setter private static boolean async = true;
    @Getter @Setter private static int updateInterval = 2;

    public NameTagHandler(JavaPlugin plugin) {

        NameTagHandler.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(new NameTagListener(), plugin);
        this.registerProvider(new DefaultNameTagProvider());

        new NameTagThread().start();
    }

    public void registerProvider(NameTagProvider newProvider) {
        providers.add(newProvider);
        providers.sort(new NameTagComparator());
    }

    public static void reloadPlayer(Player toRefresh) {

        final NameTagUpdate update = new NameTagUpdate(toRefresh);

        if (async) {
            NameTagThread.getPendingUpdates().put(update, true);
        } else {
            applyUpdate(update);
        }

    }

    public static void reloadOthersFor(Player refreshFor) {

        for (Player toRefresh : Bukkit.getOnlinePlayers()) {

            if (refreshFor != toRefresh) {
                reloadPlayer(toRefresh, refreshFor);
            }

        }

    }

    public static void reloadPlayer(Player toRefresh, Player refreshFor) {

        final NameTagUpdate update = new NameTagUpdate(toRefresh, refreshFor);

        if (async) {
            NameTagThread.getPendingUpdates().put(update, true);
        } else {
            applyUpdate(update);
        }

    }

    public static void applyUpdate(NameTagUpdate nametagUpdate) {

        final Player toRefreshPlayer = Bukkit.getPlayerExact(nametagUpdate.getToRefresh());

        if (toRefreshPlayer != null) {

            if (nametagUpdate.getRefreshFor() == null) {

                for (Player refreshFor : Bukkit.getOnlinePlayers()) {
                    reloadPlayerInternal(toRefreshPlayer,refreshFor);
                }

            } else {

                final Player refreshForPlayer = Bukkit.getPlayerExact(nametagUpdate.getRefreshFor());

                if (refreshForPlayer != null) {
                    reloadPlayerInternal(toRefreshPlayer,refreshForPlayer);
                }
            }
        }
    }

    @SneakyThrows
    public static void reloadPlayerInternal(Player toRefresh, Player refreshFor) {
        if (refreshFor.hasMetadata("NT-LoggedIn")) {
            
            NameTagInfo provided = null;

            for (int i = 0; provided == null; provided = (providers.get(i++).fetchNameTag(toRefresh,refreshFor))) {
                
            }

            Map<String,NameTagInfo> teamInfoMap = new HashMap<>();
            
            if (teamMap.containsKey(refreshFor.getName())) {
                teamInfoMap = teamMap.get(refreshFor.getName());
            }

            new ScoreboardTeamPacketMod(provided.getName(), Collections.singletonList(toRefresh.getName()), 3).sendToPlayer(refreshFor);
            
            teamInfoMap.put(toRefresh.getName(),provided);

            teamMap.put(refreshFor.getName(), teamInfoMap);
        }
    }

    public static void initiatePlayer(Player player) {

        for (NameTagInfo teamInfo : registeredTeams) {
            teamInfo.getTeamAddPacket().sendToPlayer(player);
        }

    }

    public static NameTagInfo getOrCreate(String prefix, String suffix) {

        for (NameTagInfo teamInfo : registeredTeams) {

            if (teamInfo.getPrefix().equals(prefix) && teamInfo.getSuffix().equals(suffix)) {
                //System.out.println("EQUALS");
                return teamInfo;
            }

        }

        final NameTagInfo newTeam = new NameTagInfo(String.valueOf(teamCreateIndex++), prefix, suffix);

        registeredTeams.add(newTeam);

        final ScoreboardTeamPacketMod addPacket = newTeam.getTeamAddPacket();

        for (Player player : Bukkit.getOnlinePlayers()) {
            addPacket.sendToPlayer(player);
        }

        return newTeam;
    }

}
