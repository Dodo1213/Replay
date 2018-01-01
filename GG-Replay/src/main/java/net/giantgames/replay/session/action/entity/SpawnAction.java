package net.giantgames.replay.session.action.entity;

import net.giantgames.replay.ReplayPlugin;
import net.giantgames.replay.session.ReplaySession;
import net.giantgames.replay.session.action.IAction;
import net.giantgames.replay.session.object.PacketEntity;

public class SpawnAction<E extends PacketEntity> implements IAction<E> {

    @Override
    public void apply(int velocity, E object) {
        if (velocity > 0) {
            object.sendAll();
        } else {
            object.removeAll();
        }
    }

}
