package net.minebo.practice.command.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import net.minebo.practice.arena.menu.manage.ManageMenu;
import org.bukkit.entity.Player;

public class ManageCommand extends BaseCommand {

    @CommandAlias("manage")
    @Description("Manage the practice plugin.")
    @CommandPermission("potpvp.admin")
    public void manage(Player sender) {
        new ManageMenu().openMenu(sender);
    }

}