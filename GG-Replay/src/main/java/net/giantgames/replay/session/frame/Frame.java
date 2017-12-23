package net.giantgames.replay.session.frame;

import lombok.Getter;
import net.giantgames.replay.session.action.IAction;
import net.giantgames.replay.session.object.IReplayObject;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

@Getter
public final class Frame<E extends IReplayObject> implements Serializable {

    private final IAction<E>[] actions;

    public Frame(IAction<E>[] actions) {
        this.actions = actions;
    }

    public Frame(Collection<IAction<E>> actionCollection) {
        this.actions = actionCollection.toArray(new IAction[0]);
    }

    public void play(E replayObject, int velocity) {
        for (int i = 0; i < actions.length; i++) {
            actions[i].apply(velocity, replayObject);
        }
    }

}
