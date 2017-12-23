package net.giantgames.replay.serialize;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.Serializable;

public class SerializeLocation implements Serializable {

    private String world;
    private double x, y, z;
    private float yaw, pitch;

    public SerializeLocation(Location location) {
        this.world = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public Location convert() {
        World world = Bukkit.getWorld(this.world);
        if (world == null) {
            world = Bukkit.getWorlds().get(0);
        }

        return new Location(world, x, y, z, yaw, pitch);
    }

    public static SerializeLocation from(Location location) {
        return new SerializeLocation(location);
    }

}
