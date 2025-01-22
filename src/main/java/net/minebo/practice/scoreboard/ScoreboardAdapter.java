package net.minebo.practice.scoreboard;

import net.minebo.practice.util.scoreboard.config.ScoreboardConfiguration;
import net.minebo.practice.util.scoreboard.construct.TitleGetter;

public class ScoreboardAdapter extends ScoreboardConfiguration {

    public ScoreboardAdapter() {
        this.setTitleGetter(
                new TitleGetter("&6&lPractice"));
        this.setScoreGetter(
                new MultiplexingScoreGetter(
                        new MatchScoreGetter(),
                        new LobbyScoreGetter(),
                        new EventScoreGetter()
                ));
    }

}
