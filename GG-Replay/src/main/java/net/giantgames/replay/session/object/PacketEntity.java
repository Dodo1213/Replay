package net.giantgames.replay.session.object;

import com.comphenix.packetwrapper.*;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.UUID;

@Getter
public class PacketEntity implements IReplayObject {

    protected final int entityId;
    protected final UUID uniqueId;
    protected final ItemStack[] equipment;
    protected WrappedDataWatcher dataWatcher;

    protected Location location;
    protected EntityType entityType;


    public PacketEntity(Location location) {
        this(UUID.randomUUID(), location);
    }

    public PacketEntity(UUID uuid, Location location) {
        this(location, uuid, EntityType.UNKNOWN);
    }

    public PacketEntity(Location location, UUID uuid, EntityType entityType) {
        this.entityId = (int) Math.random() * 1000;
        this.equipment = new ItemStack[Slot.values().length];
        this.entityType = entityType;
        this.location = location;
        this.uniqueId = uuid;
        this.dataWatcher = new WrappedDataWatcher();
    }

    @Override
    public PacketContainer[] send() {
        WrapperPlayServerSpawnEntity playServerSpawnEntity = new WrapperPlayServerSpawnEntity();
        playServerSpawnEntity.setType(entityType.getTypeId());
        playServerSpawnEntity.setYaw(location.getYaw());
        playServerSpawnEntity.setPitch(location.getPitch());
        playServerSpawnEntity.setX(location.getX());
        playServerSpawnEntity.setY(location.getY());
        playServerSpawnEntity.setZ(location.getZ());
        playServerSpawnEntity.setUniqueId(uniqueId);
        playServerSpawnEntity.setEntityID(entityId);

        return new PacketContainer[]{playServerSpawnEntity.getHandle()};
    }

    @Override
    public PacketContainer[] remove() {
        WrapperPlayServerEntityDestroy playServerEntityDestroy = new WrapperPlayServerEntityDestroy();
        playServerEntityDestroy.setEntityIds(new int[]{entityId});
        return new PacketContainer[]{playServerEntityDestroy.getHandle()};
    }

    public void teleport(Location location) {
        this.location = location;

        WrapperPlayServerEntityTeleport playServerEntityTeleport = new WrapperPlayServerEntityTeleport();
        playServerEntityTeleport.setEntityID(entityId);
        playServerEntityTeleport.setOnGround(true);
        playServerEntityTeleport.setX(location.getX());
        playServerEntityTeleport.setY(location.getY());
        playServerEntityTeleport.setZ(location.getZ());
        playServerEntityTeleport.setYaw(location.getYaw());
        playServerEntityTeleport.setPitch(location.getPitch());

        rotate(location.getYaw(), location.getPitch());
    }

    public void rotate(float yaw, float pitch) {
        WrapperPlayServerEntityLook playServerEntityLook = new WrapperPlayServerEntityLook();
        playServerEntityLook.setEntityID(entityId);
        playServerEntityLook.setYaw(yaw);
        playServerEntityLook.setPitch(pitch);
        playServerEntityLook.setOnGround(true);

        WrapperPlayServerEntityHeadRotation playServerEntityHeadRotation = new WrapperPlayServerEntityHeadRotation();
        playServerEntityHeadRotation.setEntityID(entityId);
        playServerEntityHeadRotation.setHeadYaw((byte) ((int) (yaw * 256.0F / 360.0F)));

        ProtocolLibrary.getProtocolManager().broadcastServerPacket(playServerEntityLook.getHandle());
        ProtocolLibrary.getProtocolManager().broadcastServerPacket(playServerEntityHeadRotation.getHandle());
    }


    public void animate(Animation animation) {
        WrapperPlayServerAnimation playServerAnimation = new WrapperPlayServerAnimation();
        playServerAnimation.setEntityID(entityId);
        playServerAnimation.setAnimation(animation.ordinal());

        ProtocolLibrary.getProtocolManager().broadcastServerPacket(playServerAnimation.getHandle());
    }

    public void status(Status status) {
        WrapperPlayServerEntityStatus playServerEntityStatus = new WrapperPlayServerEntityStatus();
        playServerEntityStatus.setEntityID(entityId);
        playServerEntityStatus.setEntityStatus(status.getStatus());

        ProtocolLibrary.getProtocolManager().broadcastServerPacket(playServerEntityStatus.getHandle());
    }

    public void equip(Slot slot, ItemStack itemStack) {
        equipment[slot.ordinal()] = itemStack;

        WrapperPlayServerEntityEquipment entityEquipment = new WrapperPlayServerEntityEquipment();
        entityEquipment.setEntityID(entityId);
        entityEquipment.setItem(itemStack);
        entityEquipment.setSlot(slot.getItemSlot());

        ProtocolLibrary.getProtocolManager().broadcastServerPacket(entityEquipment.getHandle());
    }

    public void updateMetadata() {
        WrapperPlayServerEntityMetadata playServerEntityMetadata = new WrapperPlayServerEntityMetadata();
        playServerEntityMetadata.setMetadata(dataWatcher.getWatchableObjects());
        playServerEntityMetadata.setEntityID(entityId);

        ProtocolLibrary.getProtocolManager().broadcastServerPacket(playServerEntityMetadata.getHandle());
    }

    public ItemStack getEquipment(Slot slot) {
        return equipment[slot.ordinal()];
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
