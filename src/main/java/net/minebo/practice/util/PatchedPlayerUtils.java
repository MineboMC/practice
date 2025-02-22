package net.minebo.practice.util;

import lombok.experimental.UtilityClass;
import net.minebo.practice.Practice;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// we mess with fly mode in PotPvP, so we need to reset that with PlayerUtils (in qLib)
// unfortunately, that class doesn't reset fly mode - and plugins like qHub, which use doublejump
// (implemented with fly mode if you're not familiar) have already started using that method.
@UtilityClass
public class PatchedPlayerUtils {

    public static void resetInventory(Player player) {
        resetInventory(player, null);
    }

    public static void resetInventory(Player player, GameMode gameMode) {
        resetInventory(player, gameMode, false);
    }

    public static void resetInventory(Player player, GameMode gameMode, boolean skipInvReset) {
        player.setHealth(player.getMaxHealth());
        player.setFallDistance(0F);
        player.setFoodLevel(20);
        player.setSaturation(10F);
        player.setLevel(0);
        player.setExp(0F);

        if (!skipInvReset) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
        }

        player.setFireTicks(0);

        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }

        if (gameMode != null && player.getGameMode() != gameMode) {
            player.setGameMode(gameMode);
        }

        player.setAllowFlight(false);
        player.setFlying(false);
    }

    public void denyMovement(Player player) {
        if (player.hasMetadata("noDenyMove")) {
            player.removeMetadata("noDenyMove", Practice.getInstance());
            return;
        }

        player.setWalkSpeed(0.0F);
        player.setFlySpeed(0.0F);
        player.setFoodLevel(0);
        player.setSprinting(false);
        player.setMetadata("denyMove", new FixedMetadataValue(Practice.getInstance(), true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200));
    }

    public void allowMovement(Player player) {
        if (!player.hasMetadata("denyMove")) return;
        player.setWalkSpeed(0.2F);
        player.setFlySpeed(0.2F);
        player.setFoodLevel(20);
        player.setSprinting(true);
        player.removePotionEffect(PotionEffectType.JUMP);
        player.removeMetadata("denyMove", Practice.getInstance());
    }

    public static List<String> mapToNames(Collection<UUID> uuids) {
        return uuids.stream().map(Practice.getInstance().uuidCache::name).collect(Collectors.toList());
    }

    public static List<String> mapToDisplayNames(Collection<UUID> uuids) {
        List<String> pings = new ArrayList<>();

        for(UUID uuid : uuids) {
            pings.add(Bukkit.getPlayer(uuid).getDisplayName());
        };

        return pings;
    }

    public static List<String> mapToPings(Collection<UUID> uuids) {
        List<String> pings = new ArrayList<>();

        for(UUID uuid : uuids) {
            pings.add(Bukkit.getPlayer(uuid).getPing() + "ms");
        };

        return pings;
    }

    public static String getFormattedName(UUID uuid) {
        return Practice.getInstance().getUuidCache().name(uuid);
    }
}