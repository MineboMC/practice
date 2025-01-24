package net.minebo.practice.events.menu;

import net.minebo.practice.events.enums.EventType;
import net.minebo.practice.events.menu.button.EventButton;
import net.minebo.practice.util.Callback;
import net.minebo.practice.util.InventoryUtils;
import com.google.common.base.Preconditions;
import net.minebo.practice.util.menu.Button;
import net.minebo.practice.util.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class EventSelectMenu extends Menu {

    private final boolean reset;
    private final String title;
    private final Callback<EventType> callback;

    public EventSelectMenu(Callback<EventType> callback, String title) {
        this(callback, true, title);
    }

    public EventSelectMenu(Callback<EventType> callback, boolean reset, String title) {
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
        return ChatColor.YELLOW.toString() + ChatColor.BOLD + title + "...";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        for (EventType d : EventType.values()) {
            buttons.put(index++, new EventButton(d, callback));
        }

        return buttons;
    }

}