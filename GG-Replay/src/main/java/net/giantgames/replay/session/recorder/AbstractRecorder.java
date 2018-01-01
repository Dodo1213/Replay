package net.giantgames.replay.session.recorder;

import lombok.Getter;
import net.giantgames.replay.session.recorder.result.Recording;
import net.giantgames.replay.session.frame.FrameBuilder;
import net.giantgames.replay.session.frame.Frame;
import net.giantgames.replay.session.object.IReplayObject;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

@Getter
public abstract class AbstractRecorder<E extends IReplayObject> {

    protected FrameBuilder<E> frameBuilder;
    protected Collection<Frame<? extends IReplayObject>> frames;
    protected volatile boolean started;
    protected int startFrame;
    protected volatile boolean stopped;

    public AbstractRecorder() {
        this.frameBuilder = new FrameBuilder<>();
        this.frames = Collections.synchronizedList(new LinkedList<>());
        this.started = false;
        this.stopped = false;
        this.startFrame = 0;
    }

    public synchronized void update(int frame) {
        if (stopped) {
            return;
        }

        if (!started) {
            startFrame = frame;
            started = true;
        }

        this.frames.add(snap(frame));
    }

    public abstract Frame<E> snap(int frame);

    public abstract Recording finish();

    public synchronized void stop() {
        this.stopped = true;
    }

}
