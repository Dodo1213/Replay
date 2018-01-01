package net.giantgames.replay.serialize;


import lombok.Getter;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.io.Serializable;

@Getter
public class SerializeBlockChange implements Serializable {

    private SerializeBlockVector vector;
    private SerializeBlockInfo from;
    private SerializeBlockInfo to;

    public SerializeBlockChange(Block from, Block to) {
        if (from == null && to == null) {
            this.vector = new SerializeBlockVector(new Vector(0, 0, 0));
        }
        this.vector = new SerializeBlockVector(from == null ? to.getLocation().toVector() : from.getLocation().toVector());
        this.from = SerializeBlockInfo.of(from);
        this.to = SerializeBlockInfo.of(to);
    }

    public void applyFrom(World world) {
        this.from.apply(vector.convert(), world);
    }

    public void applyTo(World world) {
        this.to.apply(vector.convert(), world);
    }


}
