package net.giantgames.replay.session.object;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        packetContainer.getIntegers().write(0, entityId);
        packetContainer.getIntegers().write(1, (int) location.getX() * 32);
        packetContainer.getIntegers().write(2, (int) location.getY() * 32);
        packetContainer.getIntegers().write(3, (int) location.getZ() * 32);
        packetContainer.getIntegers().write(4, (int) (location.getYaw() * 256.0F / 360.0F));
        packetContainer.getIntegers().write(5, (int) (location.getPitch() * 256.0F / 360.0F));

        packetWorld.add(this);
        return new PacketContainer[]{packetContainer};
    }

    @Override
    public PacketContainer[] remove() {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        packetContainer.getIntegerArrays().write(0, new int[]{entityId});

        packetWorld.remove(this);
        return new PacketContainer[]{packetContainer};
    }

    public void teleport(Location location) {
        this.location = location;
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
        packetContainer.getIntegers().write(0, entityId);
        packetContainer.getIntegers().write(1, (int) location.getX() * 32);
        packetContainer.getIntegers().write(2, (int) location.getY() * 32);
        packetContainer.getIntegers().write(3, (int) location.getZ() * 32);
        packetContainer.getBytes().write(0, (byte) (location.getYaw() * 256.0F / 360.0F));
        packetContainer.getBytes().write(1, (byte) (location.getPitch() * 256.0F / 360.0F));
        packetContainer.getBooleans().write(0, true);

        broadcast(packetContainer);

        rotate(location.getYaw(), location.getPitch());
    }

    public void rotate(float yaw, float pitch) {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_LOOK);
        packetContainer.getIntegers().write(0, entityId);
        packetContainer.getBytes().write(1, (byte) (yaw * 256.0F / 360.0F));
        packetContainer.getBytes().write(2, (byte) (pitch * 256.0F / 360.0F));
        packetContainer.getBooleans().write(0, true);

        PacketContainer container = new PacketContainer(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
        container.getIntegers().write(0, entityId);
        container.getBytes().write(0, (byte) ((int) (yaw * 256.0F / 360.0F)));
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

        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
        packetContainer.getIntegers().write(0, entityId);
        packetContainer.getItemSlots().write(0, slot.getItemSlot());
        packetContainer.getItemModifier().write(0, itemStack);

        broadcast(packetContainer);
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

        HAND(EnumWrappers.ItemSlot.MAINHAND.ordinal()),
        HELMET(EnumWrappers.ItemSlot.HEAD.ordinal()),
        CHESTPLATE(EnumWrappers.ItemSlot.CHEST.ordinal()),
        LEGGINGS(EnumWrappers.ItemSlot.LEGS.ordinal()),
        BOOTS(EnumWrappers.ItemSlot.FEET.ordinal());

        private final int slotId;

        public EnumWrappers.ItemSlot getItemSlot() {
            return EnumWrappers.ItemSlot.values()[slotId];
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
