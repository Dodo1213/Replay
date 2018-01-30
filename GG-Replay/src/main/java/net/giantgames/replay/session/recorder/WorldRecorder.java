package net.giantgames.replay.session.recorder;

import net.giantgames.replay.serialize.SerializeReplayObject;
import net.giantgames.replay.session.recorder.result.Recording;
import net.giantgames.replay.session.frame.Frame;
import net.giantgames.replay.session.object.IReplayObject;
import net.giantgames.replay.session.object.PacketWorld;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

public class WorldRecorder extends AbstractRecorder<IReplayObject> {

    private final Collection<Frame<IReplayObject>> recordedFrames;

    private final PacketWorld packetWorld;

    public WorldRecorder(PacketWorld packetWorld) {
        this.recordedFrames = new LinkedList<>();
        this.packetWorld = packetWorld;
    }

    @Override
    public Frame<IReplayObject> snap(int frame) {
        return frameBuilder.buildAndClear();
    }

    @Override
    public Recording finish() {
        return new Recording(startFrame, new SerializeReplayObject(packetWorld.getWorld().getName(), -2, null, null), frames);
    }
}
