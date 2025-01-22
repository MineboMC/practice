package net.minebo.practice.lobby.menu.statistics;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import net.minebo.basalt.api.BasaltAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import net.minebo.practice.Practice;
import net.minebo.practice.profile.elo.EloHandler;
import net.minebo.practice.util.menu.Button;

public class GlobalEloButton extends Button {

    private static EloHandler eloHandler = Practice.getInstance().getEloHandler();

    @Override
    public String getName(Player player) {
        return ChatColor.GOLD + "Global" + ChatColor.GRAY.toString() + " | " + ChatColor.WHITE + "Top 10";
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = Lists.newArrayList();

        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");

        int counter = 1;

        for (Entry<String, Integer> entry : eloHandler.topElo(null).entrySet()) {
            String color = (counter <= 3 ? ChatColor.GREEN : ChatColor.GRAY).toString();
            try {
                description.add(color + counter + ChatColor.DARK_GRAY + ". " + ChatColor.translate(BasaltAPI.INSTANCE.quickFindProfile(Bukkit.getOfflinePlayer(entry.getKey()).getUniqueId()).get().getHighestGlobalRank().getColor()) + entry.getKey() + ChatColor.GRAY + ": " + ChatColor.WHITE + entry.getValue());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }

            counter++;
        }

        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.NETHER_STAR;
    }
}
