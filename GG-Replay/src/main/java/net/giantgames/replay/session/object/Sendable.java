package net.giantgames.replay.session.object;

import com.comphenix.protocol.ProtocolLib;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public interface Sendable {

    PacketContainer[] send();


    default void sendFor(Player player) {
        PacketContainer[] containers = send();

        for (int i = 0; i < containers.length; i++) {
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, containers[i]);
            } catch (InvocationTargetException exception) {
                exception.printStackTrace();
            }
        }
    }

    default void sendAll() {
        PacketContainer[] containers = send();

        for (int i = 0; i < containers.length; i++) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, containers[i]);
                } catch (InvocationTargetException exception) {
                    exception.printStackTrace();
                }
            }

        }
    }

}
