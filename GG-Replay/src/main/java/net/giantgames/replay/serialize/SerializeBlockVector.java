package net.giantgames.replay.serialize;

import org.bukkit.util.Vector;

import java.io.Serializable;

public class SerializeBlockVector implements Serializable {

    private int x, y, z;

    public SerializeBlockVector(Vector vector) {
        this.x = vector.getBlockX();
        this.y = vector.getBlockY();
        this.z = vector.getBlockZ();
    }

    public Vector convert() {
        return new Vector(x, y, z);
    }

}
