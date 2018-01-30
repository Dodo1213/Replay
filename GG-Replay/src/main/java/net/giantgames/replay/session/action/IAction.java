package net.giantgames.replay.session.action;

import net.giantgames.replay.session.object.IReplayObject;

import java.io.Serializable;

@FunctionalInterface
public interface IAction<E extends IReplayObject> extends Serializable {

    public static final long serialVersionUID = 8327489L;

    void apply(int velocity, E object);

}
