package net.giantgames.replay.session;

import net.giantgames.replay.session.object.PacketWorld;
import org.bukkit.World;

public class RecordingSession {

    private final PacketWorld packetWorld;

    public RecordingSession(World world) {
        this.packetWorld = new PacketWorld(world);
    }


}
