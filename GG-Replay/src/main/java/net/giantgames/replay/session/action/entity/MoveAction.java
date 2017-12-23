package net.giantgames.replay.session.action.entity;

import lombok.Data;
import lombok.Getter;
import net.giantgames.replay.serialize.SerializeLocation;
import net.giantgames.replay.session.action.IAction;
import net.giantgames.replay.session.object.PacketEntity;

@Getter
@Data
public class MoveAction<E extends PacketEntity> implements IAction<E> {

    private final SerializeLocation from, to;

    public MoveAction(SerializeLocation from, SerializeLocation to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void apply(int velocity, PacketEntity object) {
        if (velocity > 0) {
            object.teleport(to.convert());
        } else {
            object.teleport(from.convert());
        }
    }
}
