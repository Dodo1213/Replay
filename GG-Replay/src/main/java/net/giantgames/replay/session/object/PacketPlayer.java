package net.giantgames.replay.session.object;

import com.comphenix.packetwrapper.*;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

@Getter
public final class PacketPlayer extends PacketEntity {

    private final WrappedGameProfile profile;

    public PacketPlayer(Location location, WrappedGameProfile profile) {
        super(profile.getUUID(), location);
        this.profile = profile;

        this.dataWatcher.setObject(6, Float.valueOf(20));
        this.dataWatcher.setObject(10, Byte.valueOf((byte) 127));
    }

    @Override
    public PacketContainer[] send() {
        WrapperPlayServerNamedEntitySpawn namedEntitySpawn = new WrapperPlayServerNamedEntitySpawn();
        namedEntitySpawn.setEntityID(entityId);
        namedEntitySpawn.setPlayerUUID(uniqueId);
        namedEntitySpawn.setPosition(location.toVector());
        namedEntitySpawn.setYaw(location.getYaw());
        namedEntitySpawn.setPitch(location.getPitch());
        namedEntitySpawn.setMetadata(dataWatcher);

        WrapperPlayServerPlayerInfo playServerPlayerInfo = new WrapperPlayServerPlayerInfo();
        playServerPlayerInfo.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        playServerPlayerInfo.setData(Arrays
                .asList(new PlayerInfoData(profile,
                        0,
                        EnumWrappers.NativeGameMode.NOT_SET,
                        WrappedChatComponent.fromText(profile.getName()))));

        return new PacketContainer[]{namedEntitySpawn.getHandle(), playServerPlayerInfo.getHandle()};
    }

    @Override
    public PacketContainer[] remove() {
        WrapperPlayServerPlayerInfo playServerPlayerInfo = new WrapperPlayServerPlayerInfo();
        playServerPlayerInfo.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
        playServerPlayerInfo.setData(Arrays
                .asList(new PlayerInfoData(profile,
                        0,
                        EnumWrappers.NativeGameMode.NOT_SET,
                        WrappedChatComponent.fromText(profile.getName()))));

        WrapperPlayServerEntityDestroy playServerEntityDestroy = new WrapperPlayServerEntityDestroy();
        playServerEntityDestroy.setEntityIds(new int[]{entityId});

        return new PacketContainer[]{playServerPlayerInfo.getHandle(), playServerEntityDestroy.getHandle()};
    }

    public void blockBreakAnimation(Block block) {
        WrapperPlayServerBlockBreakAnimation blockBreakAnimation = new WrapperPlayServerBlockBreakAnimation();
        blockBreakAnimation.setDestroyStage(block.getBlockPower());
        blockBreakAnimation.setEntityID(entityId);
        blockBreakAnimation.setLocation(new BlockPosition(block.getLocation().toVector()));
    }

}
