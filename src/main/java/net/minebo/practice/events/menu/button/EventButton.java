package net.minebo.practice.events.menu.button;

import net.minebo.practice.events.enums.EventType;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.minebo.practice.util.Callback;
import net.minebo.practice.util.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public final class EventButton extends Button {

    private final EventType eventType;
    private final Callback<EventType> callback;
    private final List<String> descriptionLines;
    private final int amount;

    public EventButton(EventType eventType, Callback<EventType> callback) {
        this(eventType, callback, ImmutableList.of(), 1);
    }

    EventButton(EventType eventType, Callback<EventType> callback, List<String> descriptionLines, int amount) {
        this.eventType = Preconditions.checkNotNull(eventType, "kitType");
        this.callback = Preconditions.checkNotNull(callback, "callback");
        this.descriptionLines = ImmutableList.copyOf(descriptionLines);
        this.amount = amount;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GOLD + eventType.getName();
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = new ArrayList<>();

        description.add(ChatColor.YELLOW + "Click here to host a " + ChatColor.YELLOW + ChatColor.BOLD + eventType.getName() + ChatColor.YELLOW + " event.");

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return eventType.getIcon();
    }

    @Override
    public int getAmount(Player player) {
        return amount;
    }

    @Override
    public byte getDamageValue(Player player) {
        return 0;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        callback.callback(eventType);
    }

}