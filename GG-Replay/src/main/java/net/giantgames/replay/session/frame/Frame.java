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

    public Frame(Queue<IAction<E>> actions) {
        this.actions = new IAction[actions.size()];
        int i = 0;
        while (!actions.isEmpty()) {
            this.actions[i++] = actions.remove();
        }
    }

    public void play(E replayObject, int velocity) {
        for (int i = 0; i < actions.length; i++) {
            System.out.printf("%s%n", actions[i].getClass().getSimpleName());
            actions[i].apply(velocity, replayObject);
        }
    }

}
