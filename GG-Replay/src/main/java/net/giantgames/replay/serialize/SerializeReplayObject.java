package net.giantgames.replay.serialize;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.giantgames.replay.session.object.IReplayObject;
import net.giantgames.replay.session.object.PacketEntity;
import net.giantgames.replay.session.object.PacketPlayer;
import net.giantgames.replay.session.object.PacketWorld;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import java.io.Serializable;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class SerializeReplayObject implements Serializable {

    private String name;
    private int entityType;
    private SerializeGameProfile gameProfile;

    public SerializeReplayObject(IReplayObject replayObject) {
        this.entityType = -2;
        this.gameProfile = null;
        this.name = "";

        if (replayObject instanceof PacketEntity) {
            PacketEntity packetEntity = (PacketEntity) replayObject;
            this.entityType = packetEntity.getEntityType().getTypeId();
            this.name = packetEntity.getUniqueId().toString();

            if (replayObject instanceof PacketPlayer) {
                this.gameProfile = new SerializeGameProfile(((PacketPlayer) replayObject).getProfile());
                this.name = ((PacketPlayer) replayObject).getProfile().getName();
            }
        } else if (replayObject instanceof PacketWorld) {
            this.name = ((PacketWorld) replayObject).getWorld().getName();
        }
    }

    public IReplayObject convert(PacketWorld packetWorld) {
        if (entityType == -2) {
            return packetWorld;
        }

        if (gameProfile != null) {
            return new PacketPlayer(packetWorld, Bukkit.getWorlds().get(0).getSpawnLocation(), gameProfile.convert());
        }

        UUID uuid = null;
        try {
            uuid = UUID.fromString(name);
        } catch (Exception exception) {
            uuid = UUID.randomUUID();

        }

        return new PacketEntity(packetWorld, Bukkit.getWorlds().get(0).getSpawnLocation(), uuid, EntityType.fromId(entityType));
    }
}