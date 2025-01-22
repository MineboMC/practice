package net.minebo.practice.scoreboard;

import java.util.List;
import java.util.function.BiConsumer;

import net.minebo.practice.events.EventHandler;
import net.minebo.practice.util.scoreboard.construct.ScoreGetter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.minebo.practice.Practice;
import net.minebo.practice.match.MatchHandler;
import net.minebo.practice.profile.setting.Setting;
import net.minebo.practice.profile.setting.SettingHandler;

final class MultiplexingScoreGetter implements ScoreGetter {

    private final BiConsumer<Player, List<String>> matchScoreGetter;
    private final BiConsumer<Player, List<String>> lobbyScoreGetter;
    private final BiConsumer<Player, List<String>> eventScoreGetter;

    MultiplexingScoreGetter(
        BiConsumer<Player, List<String>> matchScoreGetter,
        BiConsumer<Player, List<String>> lobbyScoreGetter,
        BiConsumer<Player, List<String>> eventScoreGetter
    ) {
        this.matchScoreGetter = matchScoreGetter;
        this.lobbyScoreGetter = lobbyScoreGetter;
        this.eventScoreGetter = eventScoreGetter;
    }

    @Override
    public void getScores(List<String> scores, Player player) {
        if (Practice.getInstance() == null) return;
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        SettingHandler settingHandler = Practice.getInstance().getSettingHandler();

        if (settingHandler.getSetting(player, Setting.SHOW_SCOREBOARD)) {
             scores.add("&7&m--------------------");
            if (matchHandler.isPlayingOrSpectatingMatch(player)) {
                matchScoreGetter.accept(player, scores);
            } else if (isInEvent(player)) {
                eventScoreGetter.accept(player, scores);
            } else {
                lobbyScoreGetter.accept(player, scores);
            }

            if (player.hasMetadata("ModMode")) {
                scores.add(ChatColor.GRAY.toString() + ChatColor.BOLD + "In Silent Mode");
            }

            scores.add("");
            scores.add("&eminebo.net");

            scores.add("&r&7&m--------------------");
        }
    }

    public Boolean isInEvent(Player player) {
        if (EventHandler.getCurrentEvent() != null) {
            if (EventHandler.getCurrentEvent().isPlayerInEvent(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

}