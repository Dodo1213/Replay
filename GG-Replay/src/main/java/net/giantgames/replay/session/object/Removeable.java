package net.giantgames.replay.session.object;

import com.comphenix.protocol.ProtocolLib;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public interface Removeable {

    PacketContainer[] remove();

    default void removeFor(Player player) {
        PacketContainer[] containers = remove();

        for (int i = 0; i < containers.length; i++) {
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, containers[i]);
            } catch (InvocationTargetException exception) {
                exception.printStackTrace();
            }
        }
    }

    default void removeAll() {
        PacketContainer[] containers = remove();

        for (int i = 0; i < containers.length; i++) {
            ProtocolLibrary.getProtocolManager().broadcastServerPacket(containers[i]);
        }
    }


}
