package net.giantgames.replay.session.recorder;

import net.giantgames.replay.session.frame.Frame;
import net.giantgames.replay.session.object.IReplayObject;
import net.giantgames.replay.session.object.PacketWorld;

import java.util.Collection;
import java.util.LinkedList;

public class WorldRecorder extends AbstractRecorder<IReplayObject> {

    private final Collection<Frame<IReplayObject>> recordedFrames;

    private final PacketWorld packetWorld;

    public WorldRecorder(PacketWorld packetWorld) {
        this.recordedFrames = new LinkedList<>();
        this.packetWorld = packetWorld;
    }

    public void update() {
        this.recordedFrames.add(snap());
    }

    @Override
    public Frame<IReplayObject> snap() {
        packetWorld.snap(frameBuilder);
        return frameBuilder.buildAndClear();
    }

    public IReplayObject[] finish() {
        return recordedFrames.toArray(new IReplayObject[0]);
    }

}
