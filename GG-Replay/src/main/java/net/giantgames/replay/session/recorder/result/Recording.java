package net.giantgames.replay.session.recorder.result;

import lombok.Getter;
import net.giantgames.replay.serialize.SerializeReplayObject;
import net.giantgames.replay.session.frame.Frame;
import net.giantgames.replay.session.object.IReplayObject;
import net.giantgames.replay.session.object.PacketWorld;

import java.io.Serializable;
import java.util.Collection;

@Getter
public class Recording implements Serializable {

    private final int startFrame;
    private final Frame<IReplayObject>[] frames;
    private SerializeReplayObject serializeReplayObject;
    private transient IReplayObject replayObject;

    public Recording(int startFrame, SerializeReplayObject object, Frame<IReplayObject>[] frames) {
        this.startFrame = startFrame;
        this.frames = frames;
        this.serializeReplayObject = object;
    }

    public Recording(int startFrame, SerializeReplayObject replayObject, Collection<Frame<?>> frames) {
        this.startFrame = startFrame;
        this.frames = frames.toArray(new Frame[0]);
        this.serializeReplayObject = replayObject;
    }

    public void prepare(PacketWorld packetWorld) {
        if (replayObject == null) {
            replayObject = serializeReplayObject.convert(packetWorld);
        }
    }

    public void play(int frame, int velocity) {
        int realFrame = frame - startFrame;

        if (realFrame < 0) {
            return;
        }

        if (frames.length > realFrame) {
            frames[realFrame].play(replayObject, velocity);
        }
    }

    public int getFrameCount() {
        return frames.length;
    }

}
