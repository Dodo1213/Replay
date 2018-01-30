package net.giantgames.replay.session.object;

import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerEntityStatus;
import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public final class PacketPlayer extends PacketEntity {

    private final WrappedGameProfile profile;

    public PacketPlayer(PacketWorld packetWorld, Location location, WrappedGameProfile profile) {
        super(packetWorld, profile.getUUID(), location);
        this.profile = profile;

        this.dataWatcher.setObject(6, Float.valueOf(20));
        this.dataWatcher.setObject(10, Byte.valueOf((byte) 127));
    }

    @Override
    public PacketContainer[] send() {
        PacketContainer infoContainer, spawnContainer;
        WrapperPlayServerNamedEntitySpawn spawn = new WrapperPlayServerNamedEntitySpawn();
        spawn.setMetadata(dataWatcher);
        spawn.setCurrentItem(0);
        spawn.setEntityId(entityId);
        spawn.setPitch((byte) (location.getPitch() * 256F / 360F));
        spawn.setYaw((byte) (location.getYaw() * 256F / 360F));
        spawn.setPlayerUuid(uniqueId);
        spawn.setX((int) (location.getX() * 32));
        spawn.setY((int) (location.getY() * 32));
        spawn.setZ((int) (location.getZ() * 32));
        WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo();
        info.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        info.setData(Collections.singletonList(new PlayerInfoData(profile, 0, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(profile.getName()))));
        infoContainer = info.getHandle();
        spawnContainer = spawn.getHandle();
        packetWorld.add(this);

        return new PacketContainer[]{infoContainer, spawnContainer};
    }

    @Override
    public PacketContainer[] remove() {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        packetContainer.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
        packetContainer.getPlayerInfoDataLists().write(0, Arrays
                .asList(new PlayerInfoData(profile,
                        0,
                        EnumWrappers.NativeGameMode.NOT_SET,
                        WrappedChatComponent.fromText(profile.getName()))));


        PacketContainer conatiner = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        conatiner.getIntegerArrays().write(0, new int[]{entityId});

        return new PacketContainer[]{packetContainer, conatiner};
    }
}
