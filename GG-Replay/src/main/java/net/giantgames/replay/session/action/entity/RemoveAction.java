package net.giantgames.replay.session.action.entity;

import net.giantgames.replay.session.action.IAction;
import net.giantgames.replay.session.object.PacketEntity;

public class RemoveAction<E extends PacketEntity> implements IAction<E> {

    @Override
    public void apply(int velocity, E object) {
        if (velocity > 0) {
            object.remove();
        } else {
            object.send();
        }
    }

}
