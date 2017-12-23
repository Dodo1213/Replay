package net.giantgames.replay.session.recorder;

import lombok.Getter;
import net.giantgames.replay.session.recorder.result.Recording;
import net.giantgames.replay.session.frame.FrameBuilder;
import net.giantgames.replay.session.frame.Frame;
import net.giantgames.replay.session.object.IReplayObject;

import java.util.Collection;
import java.util.LinkedList;

@Getter
public abstract class AbstractRecorder<E extends IReplayObject> {

    protected FrameBuilder<E> frameBuilder;
    protected Collection<Frame<? extends IReplayObject>> frames;
    protected boolean started;
    protected int startFrame;

    public AbstractRecorder() {
        this.frameBuilder = new FrameBuilder<>();
        this.frames = new LinkedList<>();
        started = false;
        startFrame = 0;
    }

    public void update(int frame) {
        if (!started) {
            startFrame = frame;
        }

        this.frames.add(snap(frame));
    }

    public abstract Frame<E> snap(int frame);

    public abstract Recording finish();


}
