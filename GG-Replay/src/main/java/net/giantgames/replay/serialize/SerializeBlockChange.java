package net.giantgames.replay.serialize;


import lombok.Getter;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.io.Serializable;

@Getter
public class SerializeBlockChange implements Serializable {

    private SerializeBlockVector vector;
    private SerializeBlockInfo from;
    private SerializeBlockInfo to;

    public SerializeBlockChange(Block from, Block to) {
        this.vector = new SerializeBlockVector(from.getLocation().toVector());
    }

    public void applyFrom(World world) {
        this.from.apply(vector.convert(), world);
    }

    public void applyTo(World world) {
        this.to.apply(vector.convert(), world);
    }


}
