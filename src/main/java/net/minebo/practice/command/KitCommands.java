package net.minebo.practice.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.reflect.TypeToken;
import net.minebo.practice.Practice;
import net.minebo.practice.kit.kittype.KitType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 4/8/2022
 * Project: potpvp-reprised
 */

@CommandAlias("kit")
public class KitCommands extends BaseCommand {

    @Subcommand("create")
    @Description("Creates a new kitType.")
    @CommandPermission("potpvp.admin")
    public void execute(Player player, String id) {
        if (KitType.byId(id) != null) {
            player.sendMessage(ChatColor.RED + "A kit-type by that name already exists.");
            return;
        }

        KitType kitType = new KitType(id);
        kitType.setDisplayName(id);
        kitType.setDisplayColor(ChatColor.GOLD);
        kitType.setIcon(new MaterialData(Material.DIAMOND_SWORD));
        kitType.setSort(50);
        kitType.saveAsync();

        KitType.getAllTypes().add(kitType);
        Practice.getInstance().getQueueHandler().addQueues(kitType);

        player.sendMessage(ChatColor.GREEN + "You've created a new kit-type by the ID \"" + kitType.getId() + "\".");
    }

    @Subcommand("delete")
    @Description("Deletes an existing kitType.")
    @CommandPermission("potpvp.admin")
    @CommandCompletion("@kittypes")
    public void execute(Player player, KitType kitType) {
        kitType.deleteAsync();
        KitType.getAllTypes().remove(kitType);
        Practice.getInstance().getQueueHandler().removeQueues(kitType);

        player.sendMessage(ChatColor.GREEN + "You've deleted the kit-type by the ID \"" + kitType.getId() + "\".");
    }

    @Subcommand("loadDefault")
    @Description("Load the default inventory of a kitType.")
    @CommandPermission("potpvp.admin")
    @CommandCompletion("@kittypes")
    public void loadDefaults(Player sender, KitType kitType) {
        sender.getInventory().setArmorContents(kitType.getDefaultArmor());
        sender.getInventory().setContents(kitType.getDefaultInventory());
        sender.updateInventory();

        sender.sendMessage(ChatColor.YELLOW + "Loaded default armor/inventory for " + kitType + ".");
    }

    @Subcommand("saveDefault")
    @Description("Save the default inventory of a kitType.")
    @CommandPermission("potpvp.admin")
    @CommandCompletion("@kittypes")
    public void saveDefaults(Player sender, KitType kitType) {
        kitType.setDefaultArmor(sender.getInventory().getArmorContents());
        kitType.setDefaultInventory(sender.getInventory().getContents());
        kitType.saveAsync();

        sender.sendMessage(ChatColor.YELLOW + "Saved default armor/inventory for " + kitType + ".");
    }

    @Subcommand("setdisplaycolor")
    @Description("Set a kitType's display color.")
    @CommandPermission("potpvp.admin")
    @CommandCompletion("@kittypes @chatcolors")
    public void setDisplayColor(Player sender, KitType kitType, ChatColor color) {
        kitType.setDisplayColor(color);
        kitType.saveAsync();

        sender.sendMessage(ChatColor.GREEN + "You've updated this kit-type's display color.");
    }

    @Subcommand("setdisplayname")
    @Description("Set a kitType's display name.")
    @CommandPermission("potpvp.admin")
    @CommandCompletion("@kittypes")
    public void setDisplayName(Player sender, KitType kitType, String name) {
        kitType.setDisplayName(name);
        kitType.saveAsync();

        sender.sendMessage(ChatColor.GREEN + "You've updated this kit-type's display name.");
    }

    @Subcommand("seticon")
    @Description("Set a kitType's display icon.")
    @CommandPermission("potpvp.admin")
    @CommandCompletion("@kittypes")
    public void setIcon(Player player, KitType kitType) {
        if (player.getItemInHand() == null) {
            player.sendMessage(ChatColor.RED + "Please hold an item in your hand.");
            return;
        }

        kitType.setIcon(player.getItemInHand().getData());
        kitType.saveAsync();

        player.sendMessage(ChatColor.GREEN + "You've updated this kit-type's icon.");
    }

    @Subcommand("setpriority")
    @Description("Set a kitType's sort priority.")
    @CommandPermission("potpvp.admin")
    @CommandCompletion("@kittypes")
    public void setDisplayName(Player sender, KitType kitType, int sort) {
        kitType.setSort(sort);
        kitType.saveAsync();

        KitType.getAllTypes().sort(Comparator.comparing(KitType::getSort));

        sender.sendMessage(ChatColor.GREEN + "You've updated this kit-type's sort.");
    }

    @Subcommand("import")
    @Description("Import kitTypes from json.")
    @CommandPermission("potpvp.admin")
    public void importKitTypes(CommandSender sender) {
        File file = new File(Practice.getInstance().getDataFolder(), "kitTypes.json");

        if (file.exists()) {
            try (Reader schematicsFileReader = Files.newReader(file, Charsets.UTF_8)) {
                Type schematicListType = new TypeToken<List<KitType>>() {}.getType();
                List<KitType> kitTypes = Practice.plainGson.fromJson(schematicsFileReader, schematicListType);

                for (KitType kitType : kitTypes) {
                    KitType.getAllTypes().removeIf(otherKitType -> otherKitType.getId().equals(kitType.getId()));
                    KitType.getAllTypes().add(kitType);
                    Practice.getInstance().getQueueHandler().addQueues(kitType);
                    kitType.saveAsync();
                }
            } catch (IOException e) {
                e.printStackTrace();
                sender.sendMessage(ChatColor.RED + "Failed to import.");
            }
        }

        sender.sendMessage(ChatColor.GREEN + "Imported.");
    }

    @Subcommand("export")
    @Description("Export kitTypes to json.")
    @CommandPermission("potpvp.admin")
    public void exportKitTypes(CommandSender sender) {
        String json = Practice.plainGson.toJson(KitType.getAllTypes());

        try {
            Files.write(
                    json,
                    new File(Practice.getInstance().getDataFolder(), "kitTypes.json"),
                    Charsets.UTF_8
            );

            sender.sendMessage(ChatColor.GREEN + "Exported.");
        } catch (IOException e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Failed to export.");
        }
    }

    @Subcommand("wipeKits Type")
    @Description("Wipe kitTypes for a ladder.")
    @CommandPermission("potpvp.admin")
    @CommandCompletion("@kittypes")
    public void kitWipeKitsType(Player sender, KitType kitType) {
        long modified = Practice.getInstance().getKitHandler().wipeKitsWithType(kitType);
        sender.sendMessage(ChatColor.YELLOW + "Wiped " + modified + " " + kitType.getDisplayName() + " kits.");
    }

    @Subcommand("wipeKits Player")
    @Description("Wipe kitTypes for a player.")
    @CommandPermission("potpvp.admin")
    @CommandCompletion("@players")
    public void kitWipeKitsPlayer(Player sender, UUID target) {
        Practice.getInstance().getKitHandler().wipeKitsForPlayer(target);
        sender.sendMessage(ChatColor.YELLOW + "Wiped " + Practice.getInstance().getUuidCache().name(target) + "'s kits.");
    }
}
