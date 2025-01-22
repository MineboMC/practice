package net.minebo.practice.events.menu;

import net.minebo.practice.events.enums.EventType;
import net.minebo.practice.events.menu.button.EventKitButton;
import net.minebo.practice.kit.kittype.KitType;
import net.minebo.practice.util.Callback;
import net.minebo.practice.util.InventoryUtils;
import com.google.common.base.Preconditions;
import net.minebo.practice.util.menu.Button;
import net.minebo.practice.util.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class EventKitSelectMenu extends Menu {

    private final boolean reset;
    private final String title;
    private final EventType eventType;
    private final Callback<KitType> callback;

    public EventKitSelectMenu(Callback<KitType> callback, String title, EventType eventType) {
        this(callback, true, title, eventType);
    }

    public EventKitSelectMenu(Callback<KitType> callback, boolean reset, String title, EventType eventType) {
        this.callback = Preconditions.checkNotNull(callback, "callback");
        this.reset = reset;
        this.title = title;
        this.eventType = Preconditions.checkNotNull(eventType, "eventType");
    }
    

    @Override
    public void onClose(Player player) {
        if (reset) {
            InventoryUtils.resetInventoryDelayed(player);
        }
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + title;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        for (KitType d : KitType.getAllTypes()) {
            if(eventType.getAllowedKits().contains(d.getId())) {
                buttons.put(index++, new EventKitButton(d, callback));
            }
        }

        return buttons;
    }

}