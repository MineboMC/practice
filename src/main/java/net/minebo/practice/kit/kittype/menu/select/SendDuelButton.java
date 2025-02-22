package net.minebo.practice.kit.kittype.menu.select;

import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.google.common.collect.ImmutableList;

import lombok.AllArgsConstructor;
import net.minebo.practice.util.menu.Button;
import net.minebo.practice.util.Callback;

@AllArgsConstructor
public class SendDuelButton extends Button {

    private String selectedMap; // Single selected map
    private Callback<String> mapCallback;

    @Override
    public List<String> getDescription(Player arg0) {
        return ImmutableList.of();
    }

    @Override
    public Material getMaterial(Player arg0) {
        return Material.WOOL;
    }

    @Override
    public byte getDamageValue(Player arg0) {
        return DyeColor.LIME.getWoolData();
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN + "Send duel";
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        if (selectedMap == null || selectedMap.isEmpty()) {
            player.sendMessage(ChatColor.RED + "You must select a map before sending the duel.");
            return;
        }

        mapCallback.callback(selectedMap); // Callback with the single selected map
    }
}