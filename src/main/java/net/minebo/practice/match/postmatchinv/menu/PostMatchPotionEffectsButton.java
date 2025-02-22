package net.minebo.practice.match.postmatchinv.menu;

import com.google.common.collect.ImmutableList;

import net.minebo.practice.util.menu.Button;

import net.minebo.practice.util.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.stream.Collectors;

final class PostMatchPotionEffectsButton extends Button {

    private final List<PotionEffect> effects;

    PostMatchPotionEffectsButton(List<PotionEffect> effects) {
        this.effects = ImmutableList.copyOf(effects);
    }

    @Override
    public String getName(Player player) {
        return ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Effects";
    }

    @Override
    public List<String> getDescription(Player player) {
        if (!effects.isEmpty()) {
            return effects.stream()
                    .map(effect ->
                            ChatColor.DARK_PURPLE.toString() + "* " + ChatColor.WHITE +
                                    formatEffectType(effect.getType()) +
                                    " " +
                                    (effect.getAmplifier() + 1) + // 0-indexed to 1-indexed
                                    ": " + ChatColor.LIGHT_PURPLE +
                                    TimeUtils.formatIntoMMSS(effect.getDuration() / 20) // / 20 to convert ticks to seconds
                    )
                    .collect(Collectors.toList());
        } else {
            return ImmutableList.of(
                    "",
                    ChatColor.WHITE + "No potion effects."
            );
        }
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.POTION;
    }

    private String formatEffectType(PotionEffectType type) {
        switch (type.getName().toLowerCase()) {
            case "fire_resistance": return "Fire Resistance";
            case "increase_damage": return "Strength";
            case "damage_resistance": return "Resistance";
            default: return StringUtils.capitalize(type.getName().toLowerCase());
        }
    }

}