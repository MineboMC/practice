package net.minebo.practice.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.minebo.practice.misc.Lang;
import net.minebo.practice.Practice;
import net.minebo.practice.arena.Arena;
import net.minebo.practice.arena.ArenaGrid;
import net.minebo.practice.arena.ArenaHandler;
import net.minebo.practice.arena.ArenaSchematic;
import net.minebo.practice.util.LocationUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 4/5/2022
 * Project: potpvp-reprised
 */

@CommandAlias("arena")
public class ArenaCommands extends BaseCommand {

    private static final String[] HELP_MESSAGE = {
            ChatColor.DARK_PURPLE + Lang.LONG_LINE,
            "§5§lArena Commands",
            ChatColor.DARK_PURPLE + Lang.LONG_LINE,
            "§c " + Lang.LEFT_ARROW_NAKED + " §a/arena free",
            "§c " + Lang.LEFT_ARROW_NAKED + " §a/arena createSchematic <schematic>",
            "§c " + Lang.LEFT_ARROW_NAKED + " §a/arena listArenas <schematic>",
            "§c " + Lang.LEFT_ARROW_NAKED + " §a/arena repasteSchematic <schematic>",
            "§c " + Lang.LEFT_ARROW_NAKED + " §a/arena rescaleall <schematic>",
            "§c " + Lang.LEFT_ARROW_NAKED + " §a/arena listSchematics",
            ChatColor.DARK_PURPLE + Lang.LONG_LINE,

    };

    @HelpCommand
    public void help(Player sender) {
        sender.sendMessage(HELP_MESSAGE);
    }

    @Subcommand("free")
    @Description("Free the arena grid.")
    @CommandPermission("potpvp.admin")
    public void arenaFree(Player sender) {
        Practice.getInstance().getArenaHandler().getGrid().free();
        sender.sendMessage(ChatColor.GREEN + "Arena grid has been freed.");
    }

    @Subcommand("createSchematic")
    @Description("Create and load a schematic from world edit as an arena.")
    @CommandPermission("potpvp.admin")
    @CommandCompletion("@arenaschematics")
    public void arenaCreateSchematic(Player sender, String schematicName) {
        ArenaHandler arenaHandler = Practice.getInstance().getArenaHandler();

        if (arenaHandler.getSchematic(schematicName) != null) {
            sender.sendMessage(ChatColor.RED + "Schematic " + schematicName + " already exists");
            return;
        }

        ArenaSchematic schematic = new ArenaSchematic(schematicName);
        File schemFile = schematic.getSchematicFile();

        if (!schemFile.exists()) {
            sender.sendMessage(ChatColor.RED + "No file for " + schematicName + " found. (" + schemFile.getPath() + ")");
            return;
        }

        arenaHandler.registerSchematic(schematic);

        try {
            schematic.pasteModelArena();
            arenaHandler.saveSchematics();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        sender.sendMessage(ChatColor.GREEN + "Schematic created.");
    }

    @Subcommand("listArenas")
    @Description("List all arenas.")
    @CommandPermission("potpvp.admin")
    @CommandCompletion("@arenaschematics")
    public void arenaListArenas(Player sender, String schematicName) {
        ArenaHandler arenaHandler = Practice.getInstance().getArenaHandler();
        ArenaSchematic schematic = arenaHandler.getSchematic(schematicName);

        if (schematic == null) {
            sender.sendMessage(ChatColor.RED + "Schematic " + schematicName + " not found.");
            sender.sendMessage(ChatColor.RED + "List all schematics with /arena listSchematics");
            return;
        }

        sender.sendMessage(ChatColor.RED + "------ " + ChatColor.WHITE + schematic.getName() + " Arenas" + ChatColor.RED + " ------");

        for ( Arena arena : arenaHandler.getArenas(schematic)) {
            String locationStr = LocationUtils.locToStr(arena.getSpectatorSpawn());
            String occupiedStr = arena.isInUse() ? ChatColor.RED + "In Use" : ChatColor.GREEN + "Open";

            sender.sendMessage(arena.getCopy() + ": " + ChatColor.GREEN + locationStr + ChatColor.GRAY + " - " + occupiedStr);
        }
    }

    @Subcommand("repasteSchematic")
    @Description("Repaste a schematic's arena.")
    @CommandPermission("potpvp.admin")
    @CommandCompletion("@arenaschematics")
    public void arenaRepasteSchematic(Player sender, String schematicName) {
        ArenaHandler arenaHandler = Practice.getInstance().getArenaHandler();
        ArenaSchematic schematic = arenaHandler.getSchematic(schematicName);

        if (schematic == null) {
            sender.sendMessage(ChatColor.RED + "Schematic " + schematicName + " not found.");
            sender.sendMessage(ChatColor.RED + "List all schematics with /arena listSchematics");
            return;
        }

        int currentCopies = arenaHandler.countArenas(schematic);

        if (currentCopies == 0) {
            sender.sendMessage(ChatColor.RED + "No copies of " + schematic.getName() + " exist.");
            return;
        }

        ArenaGrid arenaGrid = arenaHandler.getGrid();

        sender.sendMessage(ChatColor.GREEN + "Starting...");

        arenaGrid.scaleCopies(schematic, 0, () -> {
            sender.sendMessage(ChatColor.GREEN + "Removed old maps, creating new copies...");

            arenaGrid.scaleCopies(schematic, currentCopies, () -> {
                sender.sendMessage(ChatColor.GREEN + "Repasted " + currentCopies + " arenas using the newest " + schematic.getName() + " schematic.");
            });
        });
    }

    @Subcommand("scale")
    @Description("Scale schematics to a specific size.")
    @CommandPermission("potpvp.admin")
    @CommandCompletion("@arenaschematics")
    public void arenaScale(Player sender, String schematicName, int count) {
        ArenaHandler arenaHandler = Practice.getInstance().getArenaHandler();
        ArenaSchematic schematic = arenaHandler.getSchematic(schematicName);

        if (schematic == null) {
            sender.sendMessage(ChatColor.RED + "Schematic " + schematicName + " not found.");
            sender.sendMessage(ChatColor.RED + "List all schematics with /arena listSchematics");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "Starting...");

        arenaHandler.getGrid().scaleCopies(schematic, count, () -> {
            sender.sendMessage(ChatColor.GREEN + "Scaled " + schematic.getName() + " to " + count + " copies.");
        });
    }

    @Subcommand("rescaleall")
    @Description("Rescale all schematics and their arenas.")
    @CommandPermission("potpvp.admin")
    public void arenaRescaleAll(Player sender) {
        Practice.getInstance().getArenaHandler().getSchematics().forEach(schematic -> {
            ArenaHandler arenaHandler = Practice.getInstance().getArenaHandler();
            int totalCopies = arenaHandler.getArenas(schematic).size();

            arenaScale(sender, schematic.getName(), 0);
            arenaScale(sender, schematic.getName(), totalCopies);
        });
    }

    @Subcommand("listSchematics")
    @Description("List all practice schematics.")
    @CommandPermission("potpvp.admin")
    public void arenaListSchems(Player sender) {
        ArenaHandler arenaHandler = Practice.getInstance().getArenaHandler();
        sender.sendMessage(ChatColor.DARK_PURPLE + Lang.LONG_LINE);
        sender.sendMessage(ChatColor.translate("&5&lPotPvP Schematics"));
        sender.sendMessage(ChatColor.DARK_PURPLE + Lang.LONG_LINE);
        arenaHandler.getSchematics().forEach(schematic -> {
            int size = arenaHandler.getArenas(schematic).size();
            sender.sendMessage(ChatColor.translate("&c" + schematic.getName() + " &7| &cArenas using: &f" + size));
        });
        sender.sendMessage(ChatColor.DARK_PURPLE + Lang.LONG_LINE);
    }
}
