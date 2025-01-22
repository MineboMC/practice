package net.minebo.practice.util.nametags.packet;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public final class ScoreboardTeamPacketMod {
    private PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();

    public ScoreboardTeamPacketMod(String name, String prefix, String suffix, Collection<String> players, int mode) {
        this.packet.a = name;
        this.packet.h = mode;
        if (mode == 0 || mode == 2) {
            this.packet.b = name;
            this.packet.c = prefix;
            this.packet.d = suffix;
            this.packet.i = 3;
        }

        if (mode == 0) {
            this.addAll(players);
        }

    }

    public ScoreboardTeamPacketMod(String name, Collection<String> players, int mode) {
        if (players == null) {
            players = new ArrayList();
        }

        this.packet.a = name;
        this.packet.h = mode;
        this.addAll(players);
    }

    public void sendToPlayer(Player bukkitPlayer) {
        ((CraftPlayer)bukkitPlayer).getHandle().playerConnection.sendPacket(this.packet);
    }

    private void addAll(Collection<String> col) {
        try {
            this.packet.g.addAll(col);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
