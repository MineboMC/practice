package net.minebo.practice.profile.setting;

import com.google.common.collect.ImmutableList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Setting {

    SHOW_SCOREBOARD(
        "Match Scoreboard",
        ImmutableList.of(
            ChatColor.DARK_GRAY + "Toggles the scoreboard being enabled."
        ),
        Material.ITEM_FRAME,
        true,
        null // no permission required
    ),
    SHOW_SPECTATOR_JOIN_MESSAGES(
        "Spectator Join Messages",
        ImmutableList.of(
            ChatColor.DARK_GRAY + "Toggles seeing spectator join messages."
        ),
        Material.BONE,
        true,
        null // no permission required
    ),
    VIEW_OTHER_SPECTATORS(
        "Other Spectators",
        ImmutableList.of(
            ChatColor.DARK_GRAY + "Toggles viewing other spectators."
        ),
        Material.GLASS_BOTTLE,
        true,
        null // no permission required
    ),
    ALLOW_SPECTATORS(
            "Allow Spectators",
            ImmutableList.of(
                    ChatColor.DARK_GRAY + "Toggles the ability for others",
                    ChatColor.DARK_GRAY + "to spectate your matches."
            ),
            Material.REDSTONE_TORCH_ON,
            true,
            null // no permission required
    ),
    RECEIVE_DUELS(
        "Duel Invites",
        ImmutableList.of(
            ChatColor.DARK_GRAY + "Toggles the ability recieve",
            ChatColor.DARK_GRAY + "duel requests."
        ),
        Material.FIREBALL,
        true,
        "potpvp.toggleduels"
    ),
    VIEW_OTHERS_LIGHTNING(
        "Death Lightning",
        ImmutableList.of(
            ChatColor.DARK_GRAY + "Toggles seeing lightning",
            ChatColor.DARK_GRAY + "when other players die."
        ),
        Material.TORCH,
        true,
        "potpvp.togglelightning"
    ),
    NIGHT_MODE(
        "Night Mode",
        ImmutableList.of(
            ChatColor.DARK_GRAY + "Toggles your time being",
            ChatColor.DARK_GRAY + "changed to night time."
        ),
        Material.GLOWSTONE,
        false,
        null // no permission required
    ),
    ENABLE_GLOBAL_CHAT(
        "Global Chat",
        ImmutableList.of(
            ChatColor.DARK_GRAY + "Toggles seeing global chat messages."
        ),
        Material.BOOK_AND_QUILL,
        true,
        null // no permission required
    ),
    SEE_TOURNAMENT_JOIN_MESSAGE(
            "Tournament Join Messages",
            ImmutableList.of(
                ChatColor.DARK_GRAY + "Toggles seeing other people's",
                ChatColor.DARK_GRAY + "tournament join messages."
            ),
            Material.IRON_DOOR,
            true,
            null // no permission required
    ),
    SEE_TOURNAMENT_ELIMINATION_MESSAGES(
            "Tournament Elimination Messages",
            ImmutableList.of(
                ChatColor.DARK_GRAY + "Toggles seeing other people's",
                ChatColor.DARK_GRAY + "elimination messages."
            ),
            Material.SKULL_ITEM,
            true,
            null // no permission required
    );

    /**
     * Friendly (colored) display name for this setting
     */
    @Getter private final String name;

    /**
     * Friendly (colored) description for this setting
     */
    @Getter private final List<String> description;

    /**
     * Material to be used when rendering an icon for this setting
     */
    @Getter private final Material icon;

    /**
     * Default value for this setting, will be used for players who haven't
     * updated the setting and if a player's settings fail to load.
     */
    private final boolean defaultValue;

    /**
     * The permission required to be able to see and update this setting,
     * null means no permission is required to update/see.
     */
    private final String permission;

    // Using @Getter means the method would be 'isDefaultValue',
    // which doesn't correctly represent this variable.
    public boolean getDefaultValue() {
        return defaultValue;
    }

    public boolean canUpdate(Player player) {
        return permission == null || player.hasPermission(permission);
    }

}