package net.giantgames.replay.serialize;

import org.bukkit.util.Vector;

import java.io.Serializable;

public class SerializeVector implements Serializable {

    private double x, y, z;

    public SerializeVector(Vector vector) {
        this.x = vector.getX();
        this.y = vector.getY();
        this.z = vector.getZ();
    }

    public Vector convert() {
        return new Vector(x, y, z);
    }

}
