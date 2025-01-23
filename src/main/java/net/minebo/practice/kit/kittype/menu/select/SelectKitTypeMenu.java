package net.minebo.practice.kit.kittype.menu.select;

import com.google.common.base.Preconditions;

import net.minebo.practice.Practice;
import net.minebo.practice.kit.kittype.KitType;
import net.minebo.practice.util.Callback;
import net.minebo.practice.party.Party;
import net.minebo.practice.util.InventoryUtils;
import net.minebo.practice.util.menu.Button;
import net.minebo.practice.util.menu.Menu;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public final class SelectKitTypeMenu extends Menu {

    private final boolean reset;
    private final String title;
    private final Callback<KitType> callback;

    public SelectKitTypeMenu(Callback<KitType> callback, String title) {
        this(callback, true, title);
        setPlaceholder(true);
    }

    public SelectKitTypeMenu(Callback<KitType> callback, boolean reset, String title) {
        this.callback = Preconditions.checkNotNull(callback, "callback");
        this.reset = reset;
        this.title = title;
    }
    

    @Override
    public void onClose(Player player) {
        if (reset) {
            InventoryUtils.resetInventoryDelayed(player);
        }
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.YELLOW.toString() + ChatColor.BOLD + title;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int x = 1;
        int y = 0;

        for (KitType kitType : KitType.getAllTypes()) {

            if (!player.isOp() && kitType.isHidden()) {
                continue;
            }
            buttons.put(getSlot(x, y), new KitTypeButton(kitType, callback));

            x++;

            if (x == 8) {
                y++;
                x = 1;
            }

        }

        Party party = Practice.getInstance().getPartyHandler().getParty(player);
        if (party != null) {
            buttons.put(getSlot(x, y), new KitTypeButton(KitType.teamFight, callback));
        }

        return buttons;
    }

}