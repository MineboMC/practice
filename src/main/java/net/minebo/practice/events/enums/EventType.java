package net.minebo.practice.events.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@AllArgsConstructor
public enum EventType {
    SUMO(
            "Sumo",
            Material.STICK,
            2,
            50,
            true,
            true,
            false,
            null,
            "potpvp.host.sumo"
    ),
    LMS(
            "LMS",
            Material.DIAMOND_SWORD,
            2,
            64,
            true,
            true,
            true,
            new ArrayList<>(Arrays.asList(
                    "NODEBUFF",
                    "SOUP"
            )),
            "potpvp.host.lms"
    ),
    DEATHRACE(
            "Death Race", // Display Name
            Material.SKULL_ITEM, // Display Icon
            2, // Min players
            50, // Max Players
            false, // Should fighters see spectators
            false, // Should people be teleported to the arena during the waiting period
            false, // Should the host be able to pick the kit
            null,
            "potpvp.host.deathrace"
    ),
    OITC(
            "OITC", // Display Name
            Material.BOW, // Display Icon
            2, // Min players
                    72, // Max Players
                    false, // Should fighters see spectators
                    false, // Should people be teleported to the arena during the waiting period
                    false, // Should the host be able to pick the kit
                    null,
                    "potpvp.host.oitc"
    );


    @Getter private final String name;
    @Getter private Material icon;

    @Getter private int minPlayers;
    @Getter private int maxPlayers;

    @Getter private boolean spectatorsVisible;
    @Getter private boolean teleportOnJoin;

    @Getter private boolean allowKitSelection;
    @Getter private List<String> allowedKits;

    @Getter private String permission;
}

