package net.giantgames.replay.session.recorder;

import lombok.Getter;
import net.giantgames.replay.session.frame.FrameBuilder;
import net.giantgames.replay.session.frame.Frame;
import net.giantgames.replay.session.object.IReplayObject;

import java.util.Collection;
import java.util.LinkedList;

@Getter
public abstract class AbstractRecorder<E extends IReplayObject> {

    protected FrameBuilder<E> frameBuilder;

    public AbstractRecorder() {
        this.frameBuilder = new FrameBuilder<>();
    }

    public abstract Frame<E> snap();


}
