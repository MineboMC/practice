package net.minebo.practice.match.postmatchinv.menu;

import com.google.common.base.Preconditions;

import net.minebo.practice.Practice;
import net.minebo.practice.match.postmatchinv.PostMatchPlayer;
import net.minebo.practice.util.menu.Button;
import com.google.common.collect.ImmutableList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

final class PostMatchSwapTargetButton extends Button {

    private final PostMatchPlayer newTarget;

    PostMatchSwapTargetButton(PostMatchPlayer newTarget) {
        this.newTarget = Preconditions.checkNotNull(newTarget, "newTarget");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN + "View " + Practice.getInstance().getUuidCache().name(newTarget.getPlayerUuid()) + "'s inventory";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            "",
            ChatColor.YELLOW + "Swap your view to " + Practice.getInstance().getUuidCache().name(newTarget.getPlayerUuid()) + "'s inventory"
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.LEVER;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        new PostMatchMenu(newTarget).openMenu(player);
    }

}