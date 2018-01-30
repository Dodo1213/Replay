package net.giantgames.replay.session.object;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Getter
public class PacketItem extends PacketEntity {

    private static Method getNMSCopy;

    static {
        String version = Bukkit.getServer().getClass().getPackage().getName();
        version = version.substring(version.lastIndexOf('.') + 1);
        try {
            getNMSCopy = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack").getDeclaredMethod("asNMSCopy", ItemStack.class);
        } catch (NoSuchMethodException | ClassNotFoundException e) {
        }
    }

    private ItemStack itemStack;

    public PacketItem(PacketWorld packetWorld, Location location, ItemStack stack) {
        super(packetWorld, location);
        this.itemStack = stack;
        this.entityType = EntityType.DROPPED_ITEM;
        try {
            dataWatcher.setObject(6, WrappedDataWatcher.Registry.getItemStackSerializer(false), getNMSCopy.invoke(null, itemStack));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
