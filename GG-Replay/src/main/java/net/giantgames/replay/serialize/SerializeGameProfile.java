package net.giantgames.replay.serialize;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

public class SerializeGameProfile implements Serializable {

    private UUID uuid;
    private String name, texture, signature;

    public SerializeGameProfile(Player player) {
        this(WrappedGameProfile.fromPlayer(player));
    }

    public SerializeGameProfile(WrappedGameProfile wrappedGameProfile) {
        this.uuid = wrappedGameProfile.getUUID();
        this.name = wrappedGameProfile.getName();

        Collection<WrappedSignedProperty> propertyCollection = wrappedGameProfile.getProperties().get("texture");
        for (WrappedSignedProperty signedProperty : propertyCollection) {
            this.texture = signedProperty.getName();
            this.signature = signedProperty.getSignature();
        }
    }

    public WrappedGameProfile convert() {
        WrappedGameProfile wrappedGameProfile = new WrappedGameProfile(uuid, name);
        if (texture != null && signature != null) {
            wrappedGameProfile.getProperties().put("texture", new WrappedSignedProperty("texture", texture, signature));
        }
        return wrappedGameProfile;
    }

}
