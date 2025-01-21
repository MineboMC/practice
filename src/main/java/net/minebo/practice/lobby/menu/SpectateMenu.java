package net.minebo.practice.lobby.menu;

import net.minebo.practice.Practice;
import net.minebo.practice.match.Match;
import net.minebo.practice.match.MatchState;
import net.minebo.practice.match.MatchTeam;
import net.minebo.practice.profile.setting.Setting;
import net.minebo.practice.profile.setting.SettingHandler;
import net.minebo.practice.util.menu.Button;
import net.minebo.practice.util.menu.pagination.PaginatedMenu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SpectateMenu extends PaginatedMenu {
    
    public SpectateMenu() {
        setAutoUpdate(true);
    }
    
    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Spectate a match";
    }
    
    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        SettingHandler settingHandler = Practice.getInstance().getSettingHandler();
        Map<Integer, Button> buttons = new HashMap<>();
        int i = 0;
        
        for (Match match : Practice.getInstance().getMatchHandler().getHostedMatches()) {
            // players can view this menu while spectating
            if (match.isSpectator(player.getUniqueId())) {
                continue;
            }
            
            if (match.getState() == MatchState.ENDING) {
                continue;
            }
            
            if (!Practice.getInstance().getTournamentHandler().isInTournament(match)) {
                int numTotalPlayers = 0;
                int numSpecDisabled = 0;
                
                for (MatchTeam team : match.getTeams()) {
                    for (UUID member : team.getAliveMembers()) {
                        numTotalPlayers++;
                        
                        if (!settingHandler.getSetting(Bukkit.getPlayer(member), Setting.ALLOW_SPECTATORS)) {
                            numSpecDisabled++;
                        }
                    }
                }
                
                // if >= 50% of participants have spectators disabled
                // we won't render this match in the menu
                if ((float) numSpecDisabled / (float) numTotalPlayers >= 0.5) {
                    continue;
                }
            }
            
            buttons.put(i++, new SpectateButton(match));
        }
        
        return buttons;
    }

    // we lock the size of this inventory at full, otherwise we'll have
    // issues if it 'grows' into the next line while it's open (say we open
    // the menu with 8 entries, then it grows to 11 [and onto the second row]
    // - this breaks things)
    @Override
    public int size(Player player) {
        return 9 * 6;
    }
    
}