package net.minebo.practice.util.nametags.construct;

import lombok.Getter;
import net.minebo.practice.util.nametags.packet.ScoreboardTeamPacketMod;

import java.util.ArrayList;

public final class NameTagInfo {

    @Getter public String name;
    @Getter public String prefix;
    @Getter public String suffix;

    @Getter public ScoreboardTeamPacketMod teamAddPacket;

    public NameTagInfo(String name,String prefix,String suffix) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;

        this.teamAddPacket = new ScoreboardTeamPacketMod(name, prefix, suffix, new ArrayList<String>(), 0);
    }

    @Override
    public boolean equals(Object other) {

        if (other instanceof NameTagInfo) {

            final NameTagInfo otherNametag = (NameTagInfo) other;

            return (this.name.equals(otherNametag.name) && this.prefix.equals(otherNametag.prefix) && this.suffix.equals(otherNametag.suffix));
        }

        return false;
    }

}