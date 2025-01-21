package net.minebo.practice.party.menu.otherparties;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.minebo.practice.Practice;
import net.minebo.practice.lobby.LobbyHandler;
import net.minebo.practice.party.Party;
import net.minebo.practice.party.PartyHandler;
import net.minebo.practice.profile.setting.Setting;
import net.minebo.practice.profile.setting.SettingHandler;
import net.minebo.practice.util.menu.Button;
import net.minebo.practice.util.menu.pagination.PaginatedMenu;

public final class OtherPartiesMenu extends PaginatedMenu {

    public OtherPartiesMenu() {
        setPlaceholder(true);
        setAutoUpdate(true);
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Other parties";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        SettingHandler settingHandler = Practice.getInstance().getSettingHandler();
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        LobbyHandler lobbyHandler = Practice.getInstance().getLobbyHandler();

        Map<Integer, Button> buttons = new HashMap<>();
        List<Party> parties = new ArrayList<>(partyHandler.getParties());
        int index = 0;

        parties.sort(Comparator.comparing(p -> p.getMembers().size()));

        for (Party party : parties) {
            if (party.isMember(player.getUniqueId())) {
                continue;
            }

            if (!lobbyHandler.isInLobby(Bukkit.getPlayer(party.getLeader()))) {
                continue;
            }

            if (!settingHandler.getSetting(Bukkit.getPlayer(party.getLeader()), Setting.RECEIVE_DUELS)) {
                continue;
            }

            /* if (PotPvPRP.getInstance().getTournamentHandler().isInTournament(party)) {
                continue;
            } */

            buttons.put(index++, new OtherPartyButton(party));
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

    @Override
    public int getMaxItemsPerPage(Player player) {
        return 9 * 5; // top row is dedicated to switching
    }
}