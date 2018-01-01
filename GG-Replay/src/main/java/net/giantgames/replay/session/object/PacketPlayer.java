package net.giantgames.replay.session.object;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import lombok.Getter;
import org.bukkit.Location;

import java.util.Arrays;

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
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
        packetContainer.getIntegers().write(0, entityId);
        packetContainer.getIntegers().write(1, (int) location.getX() * 32);
        packetContainer.getIntegers().write(2, (int) location.getY() * 32);
        packetContainer.getIntegers().write(3, (int) location.getZ() * 32);
        packetContainer.getBytes().write(0, (byte) (location.getYaw() * 256.0F / 360.0F));
        packetContainer.getBytes().write(1, (byte) (location.getPitch() * 256.0F / 360.0F));
        packetContainer.getUUIDs().write(0, uniqueId);
        packetContainer.getDataWatcherModifier().write(0, dataWatcher);


        PacketContainer container = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        container.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        container.getPlayerInfoDataLists().write(0, Arrays
                .asList(new PlayerInfoData(profile,
                        0,
                        EnumWrappers.NativeGameMode.NOT_SET,
                        WrappedChatComponent.fromText(profile.getName()))));

        packetWorld.add(this);

        return new PacketContainer[]{packetContainer, container};
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
