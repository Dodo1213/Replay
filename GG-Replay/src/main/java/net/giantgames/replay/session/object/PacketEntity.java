package net.giantgames.replay.session.object;

import com.comphenix.packetwrapper.WrapperPlayServerEntityEquipment;
import com.comphenix.packetwrapper.WrapperPlayServerRelEntityMove;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntity;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.StreamSerializer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class PacketEntity implements IReplayObject {

    protected final int entityId;
    protected final UUID uniqueId;
    protected final ItemStack[] equipment;
    protected final PacketWorld packetWorld;
    protected final WrappedDataWatcher dataWatcher;

    protected Location location;
    protected EntityType entityType;


    public PacketEntity(PacketWorld packetWorld, Location location) {
        this(packetWorld, UUID.randomUUID(), location);
    }

    public PacketEntity(PacketWorld packetWorld, UUID uuid, Location location) {
        this(packetWorld, location, uuid, EntityType.UNKNOWN);
    }

    public PacketEntity(PacketWorld packetWorld, Location location, UUID uuid, EntityType entityType) {
        this.entityId = ThreadLocalRandom.current().nextInt(1000) + 3000;
        this.equipment = new ItemStack[Slot.values().length];
        this.entityType = entityType;
        this.location = location;
        this.uniqueId = uuid;
        this.dataWatcher = new WrappedDataWatcher();
        this.packetWorld = packetWorld;
    }

    @Override
    public PacketContainer[] send() {
        PacketContainer s;
        System.out.println("Spawn: "+entityType);
        if (entityType.isAlive()) {
            WrapperPlayServerSpawnEntityLiving spawn = new WrapperPlayServerSpawnEntityLiving();
            spawn.setEntityId(entityId);
            spawn.setHeadPitch((int) (location.getYaw() * 256.0F / 360.0F));
            spawn.setPitch((int) (location.getPitch() * 256.0F / 360.0F));
            spawn.setYaw((int) (location.getYaw() * 256.0F / 360.0F));
            spawn.setMetadata(getDataWatcher());
            spawn.setType(entityType.getTypeId());
            spawn.setVelocityX(0);
            spawn.setVelocityY(0);
            spawn.setVelocityZ(0);
            spawn.setX(floor(location.getX() * 32D));
            spawn.setY(floor(location.getY() * 32D));
            spawn.setZ(floor(location.getZ() * 32D));
            s = spawn.getHandle();
        } else {
            s = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
            s.getIntegers().write(0, entityId);
            s.getIntegers().write(1, floor(location.getX() * 32D));
            s.getIntegers().write(2, floor(location.getY() * 32D));
            s.getIntegers().write(3, floor(location.getZ() * 32D));
            s.getIntegers().write(4, 0);
            s.getIntegers().write(5, 0);
            s.getIntegers().write(6, 0);
            s.getIntegers().write(7, (int) (location.getPitch() * 256.0F / 360.0F));
            s.getIntegers().write(8, (int) (location.getYaw() * 256.0F / 360.0F));
            s.getIntegers().write(9, (int) entityType.getTypeId());
            s.getIntegers().write(10, 0);
        }
        packetWorld.add(this);
        return new PacketContainer[]{s};
    }

    @Override
    public PacketContainer[] remove() {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        packetContainer.getIntegerArrays().write(0, new int[]{entityId});

        packetWorld.remove(this);
        return new PacketContainer[]{packetContainer};
    }

    public void teleport(Location location) {
        Location pref = this.location.clone();
        int deltaX = floor(location.getX() * 32D) - floor(pref.getX() * 32D);
        int deltaY = floor(location.getY() * 32D) - floor(pref.getY() * 32D);
        int deltaZ = floor(location.getZ() * 32D) - floor(pref.getZ() * 32D);
        this.location = location;
        PacketContainer packetContainer;
        if (deltaX >= -128 && deltaX < 128 && deltaY >= -128 && deltaY < 128 && deltaZ >= -128 && deltaZ < 128) {
            WrapperPlayServerRelEntityMove move = new WrapperPlayServerRelEntityMove();
            move.setEntityId(entityId);
            move.setOnGround(true);
            move.getHandle().getBytes().write(0, (byte) deltaX);
            move.getHandle().getBytes().write(1, (byte) deltaY);
            move.getHandle().getBytes().write(2, (byte) deltaZ);
            packetContainer = move.getHandle();
        } else {
            packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
            packetContainer.getIntegers().write(0, entityId);
            packetContainer.getIntegers().write(1, floor(location.getX() * 32));
            packetContainer.getIntegers().write(2, floor(location.getY() * 32));
            packetContainer.getIntegers().write(3, floor(location.getZ() * 32));
            packetContainer.getBytes().write(0, (byte) (location.getYaw() * 256.0F / 360.0F));
            packetContainer.getBytes().write(1, (byte) (location.getPitch() * 256.0F / 360.0F));
            packetContainer.getBooleans().write(0, true);
        }
        broadcast(packetContainer);
        rotate(location.getYaw(), location.getPitch());
    }

    private int floor(double value) {
        int i = (int) value;
        return value < (double) i ? i - 1 : i;
    }

    public void rotate(float yaw, float pitch) {
        byte decodedYaw = (byte) (yaw * 256.0F / 360.0F);
        byte decodedPitch = (byte) (pitch * 256.0F / 360.0F);
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_LOOK);
        packetContainer.getIntegers().write(0, entityId);
        packetContainer.getBytes().write(3, decodedYaw);
        packetContainer.getBytes().write(4, decodedPitch);
        packetContainer.getBooleans().write(0, true);

        PacketContainer container = new PacketContainer(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
        container.getIntegers().write(0, entityId);
        container.getBytes().write(0, (byte) (floor(yaw * 256.0F / 360.0F)));
        broadcast(packetContainer, container);
    }


    public void animate(Animation animation) {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ANIMATION);
        packetContainer.getIntegers().write(0, entityId);
        packetContainer.getIntegers().write(1, animation.ordinal());

        broadcast(packetContainer);
    }

    public void status(Status status) {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_STATUS);
        packetContainer.getIntegers().write(0, entityId);
        packetContainer.getBytes().write(0, status.getStatus());
        broadcast(packetContainer);
    }

    public void equip(Slot slot, ItemStack itemStack) {
        equipment[slot.ordinal()] = itemStack;
/*        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
        packetContainer.getIntegers().write(0, entityId);
        packetContainer.getItemSlots().write(0, slot.getItemSlot());
        packetContainer.getItemModifier().write(0, itemStack);*/

        WrapperPlayServerEntityEquipment e = new WrapperPlayServerEntityEquipment();
        e.setEntityid(entityId);
        e.setItem(itemStack);
        e.setSlot(slot.slotId);
        broadcast(e.getHandle());
    }

    public void updateMetadata() {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        packetContainer.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());
        packetContainer.getIntegers().write(0, entityId);
        broadcast(packetContainer);
    }

    public ItemStack getEquipment(Slot slot) {
        return equipment[slot.ordinal()];
    }

    protected void broadcast(PacketContainer... packets) {
        for (int i = 0; i < packets.length; i++) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, packets[i]);
                } catch (InvocationTargetException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    @Getter
    @AllArgsConstructor
    public enum Slot implements Serializable {

        HAND(0),
        HELMET(4),
        CHESTPLATE(3),
        LEGGINGS(2),
        BOOTS(1);

        private final int slotId;

        public EnumWrappers.ItemSlot getItemSlot() {
            if (this == HAND) {
                return EnumWrappers.ItemSlot.MAINHAND;
            } else if (this == HELMET) {
                return EnumWrappers.ItemSlot.HEAD;
            } else if (this == CHESTPLATE) {
                return EnumWrappers.ItemSlot.CHEST;
            } else if (this == LEGGINGS) {
                return EnumWrappers.ItemSlot.LEGS;
            } else if (this == BOOTS) {
                return EnumWrappers.ItemSlot.FEET;
            } else {
                return null;
            }
        }

        public static Slot fromArmorSlot(int slot) {
            switch (slot) {
                case 0:
                    return HELMET;
                case 1:
                    return CHESTPLATE;
                case 2:
                    return LEGGINGS;
                default:
                    return BOOTS;
            }
        }
    }

    public enum Animation implements Serializable {
        SWING_MAIN_ARM,
        TAKE_DAMAGE,
        LEAVE_BED,
        SWING_OFFHAND,
        CRITICAL_EFFECT,
        MAGIC_CRITICAL_EFFECT;
    }

    @Getter
    @AllArgsConstructor
    public enum Status implements Serializable {

        HURT((byte) 2),
        DEATH((byte) 3),
        FINISH_USE((byte) 9);

        private byte status;

    }

    @Override
    public int hashCode() {
        return uniqueId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof PacketEntity)) {
            return false;
        }

        return ((PacketEntity) obj).getUniqueId().equals(this.uniqueId);

    }
}
