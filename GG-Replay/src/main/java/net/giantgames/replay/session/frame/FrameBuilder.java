package net.giantgames.replay.session.frame;

import com.google.common.collect.Queues;
import net.giantgames.replay.session.action.IAction;
import net.giantgames.replay.session.object.IReplayObject;

import java.util.LinkedList;
import java.util.Queue;

public class FrameBuilder<E extends IReplayObject> {

    private Queue<IAction<? extends E>> queue;

    public FrameBuilder() {
        this.queue = Queues.synchronizedQueue(new LinkedList<>());
    }

    public FrameBuilder<E> add(IAction<? extends E> action) {
        this.queue.add(action);
        return this;
    }

    public FrameBuilder<E> add(Frame<? extends E> frame) {
        IAction<? extends E>[] actions = frame.getActions();
        for (int i = 0; i < actions.length; i++) {
            this.queue.add(actions[i]);
        }
        return this;
    }

    public FrameBuilder<E> clear() {
        this.queue.clear();
        return this;
    }

    public Frame<E> buildAndClear() {
        Frame<E> frame = build();
        clear();
        return frame;
    }

    public Frame<E> build() {
        return new Frame(queue);
    }

}
