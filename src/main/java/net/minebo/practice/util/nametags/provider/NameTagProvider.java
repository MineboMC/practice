package net.minebo.practice.util.nametags.provider;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minebo.practice.util.nametags.NameTagHandler;
import net.minebo.practice.util.nametags.construct.NameTagInfo;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;

@AllArgsConstructor
public abstract class NameTagProvider {

    @Getter private String name;
    @Getter private int weight;

    public abstract NameTagInfo fetchNameTag(Player toRefresh, Player refreshFor) throws ExecutionException, InterruptedException;

    public final NameTagInfo createNameTag(String prefix,String suffix) {
        return NameTagHandler.getOrCreate(prefix, suffix);
    }

}