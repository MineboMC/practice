package net.minebo.practice.kit.kittype.menu.select;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import net.minebo.basalt.util.Chat;
import net.minebo.practice.kit.kittype.KitType;
import net.minebo.practice.util.Callback;
import net.minebo.practice.util.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

final class KitTypeButton extends Button {

    private final KitType kitType;
    private final Callback<KitType> callback;
    private final List<String> descriptionLines;
    private final int amount;

    KitTypeButton(KitType kitType, Callback<KitType> callback) {
        this(kitType, callback, ImmutableList.of(), 1);
    }

    KitTypeButton(KitType kitType, Callback<KitType> callback, List<String> descriptionLines, int amount) {
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
        this.callback = Preconditions.checkNotNull(callback, "callback");
        this.descriptionLines = ImmutableList.copyOf(descriptionLines);
        this.amount = amount;
    }

    @Override
    public String getName(Player player) {
        return kitType.getDisplayColor() + ChatColor.BOLD.toString() + kitType.getDisplayName();
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = new ArrayList<>();

        if (kitType.isHidden()) {
            description.add(ChatColor.GRAY + "Hidden from normal players");
        }

        if (!descriptionLines.isEmpty()) {
            if (!(description.isEmpty())) {
                description.add("");
            }
            description.addAll(descriptionLines);
        }

        if(description.isEmpty()) {
            description.add("");
            description.add(ChatColor.YELLOW + "Click here to select!");
        } else {
            description.add("");
            description.add(ChatColor.YELLOW + "Click here to play!");
        }

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return kitType.getIcon().getItemType();
    }

    @Override
    public int getAmount(Player player) {
        return amount;
    }

    @Override
    public byte getDamageValue(Player player) {
        return kitType.getIcon().getData();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        callback.callback(kitType);
    }

}