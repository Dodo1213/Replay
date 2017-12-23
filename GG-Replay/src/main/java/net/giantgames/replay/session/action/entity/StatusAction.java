package net.giantgames.replay.session.action.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.giantgames.replay.session.action.IAction;
import net.giantgames.replay.session.object.PacketEntity;

@Getter
@AllArgsConstructor
public class StatusAction<E extends PacketEntity> implements IAction<E> {

    private final PacketEntity.Status status;

    @Override
    public void apply(int velocity, E object) {
        object.status(status);
    }
}
