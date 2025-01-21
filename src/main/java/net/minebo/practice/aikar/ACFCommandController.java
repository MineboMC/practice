package net.minebo.practice.aikar;

import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import net.minebo.practice.Practice;
import net.minebo.practice.aikar.completion.ChatColorCompletionHandler;
import net.minebo.practice.aikar.completion.KitTypeCompletionHandler;
import net.minebo.practice.aikar.completion.PlayerCompletionHandler;
import net.minebo.practice.aikar.completion.SchematicCompletionHandler;
import net.minebo.practice.aikar.context.*;
import net.minebo.practice.command.*;
import net.minebo.practice.command.duel.AcceptCommand;
import net.minebo.practice.command.duel.DuelCommand;
import net.minebo.practice.command.match.LeaveCommand;
import net.minebo.practice.command.match.MapCommand;
import net.minebo.practice.command.match.SpectateCommand;
import net.minebo.practice.command.misc.*;
import net.minebo.practice.command.settings.*;
import net.minebo.practice.command.settings.TEMCommand;
import net.minebo.practice.command.silent.FollowCommand;
import net.minebo.practice.command.silent.SilentCommand;
import net.minebo.practice.command.silent.SilentFollowCommand;
import net.minebo.practice.command.silent.UnfollowCommand;
import net.minebo.practice.command.stats.EloSetCommands;
import net.minebo.practice.command.stats.StatsResetCommands;
import net.minebo.practice.kit.kittype.KitType;
import org.bukkit.ChatColor;

import java.util.UUID;

public class ACFCommandController {

    public static void registerAll() {

        Practice.getInstance().setCommandController(new PaperCommandManager(Practice.getInstance()));

        Practice.getInstance().getCommandController().getCommandReplacements().addReplacement("vanishcheck", "false");

        Practice.getInstance().getCommandController().getCommandContexts().registerContext(ChatColor.class, new ChatColorContextResolver());
        Practice.getInstance().getCommandController().getCommandContexts().registerContext(UUID.class, new UUIDContextResolver());
        Practice.getInstance().getCommandController().getCommandContexts().registerContext(KitType.class, new KitTypeContextResolver());
        Practice.getInstance().getCommandController().getCommandContexts().registerContext(OnlinePlayer.class, new OnlinePlayerResolver());

        Practice.getInstance().getCommandController().getCommandCompletions().registerCompletion("arenaschematics", new SchematicCompletionHandler());
        Practice.getInstance().getCommandController().getCommandCompletions().registerCompletion("kittypes", new KitTypeCompletionHandler());
        Practice.getInstance().getCommandController().getCommandCompletions().registerCompletion("chatcolors", new ChatColorCompletionHandler());
        Practice.getInstance().getCommandController().getCommandCompletions().registerCompletion("players", new PlayerCompletionHandler());

        Practice.getInstance().getCommandController().registerCommand(new AcceptCommand());
        Practice.getInstance().getCommandController().registerCommand(new DuelCommand());

        Practice.getInstance().getCommandController().registerCommand(new LeaveCommand());
        Practice.getInstance().getCommandController().registerCommand(new MapCommand());
        Practice.getInstance().getCommandController().registerCommand(new SpectateCommand());

        Practice.getInstance().getCommandController().registerCommand(new BuildCommand());
        Practice.getInstance().getCommandController().registerCommand(new CheckPostMatchInvCommand());
        Practice.getInstance().getCommandController().registerCommand(new TJMCommand());
        Practice.getInstance().getCommandController().registerCommand(new TEMCommand());
        Practice.getInstance().getCommandController().registerCommand(new HelpCommand());
        Practice.getInstance().getCommandController().registerCommand(new ManageCommand());

        Practice.getInstance().getCommandController().registerCommand(new TimeCommands());
        Practice.getInstance().getCommandController().registerCommand(new SettingsCommand());
        Practice.getInstance().getCommandController().registerCommand(new ToggleDuelCommand());
        Practice.getInstance().getCommandController().registerCommand(new ToggleGlobalChatCommand());

        Practice.getInstance().getCommandController().registerCommand(new FollowCommand());
        Practice.getInstance().getCommandController().registerCommand(new SilentCommand());
        Practice.getInstance().getCommandController().registerCommand(new SilentFollowCommand());
        Practice.getInstance().getCommandController().registerCommand(new UnfollowCommand());

        Practice.getInstance().getCommandController().registerCommand(new EloSetCommands());
        Practice.getInstance().getCommandController().registerCommand(new StatsResetCommands());

        Practice.getInstance().getCommandController().registerCommand(new ArenaCommands());
        Practice.getInstance().getCommandController().registerCommand(new KitCommands());
        Practice.getInstance().getCommandController().registerCommand(new MatchCommands());
        Practice.getInstance().getCommandController().registerCommand(new PartyCommands());
        Practice.getInstance().getCommandController().registerCommand(new TournamentCommands());

    }

}
