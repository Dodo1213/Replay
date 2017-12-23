package net.giantgames.replay.session.action.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.giantgames.replay.session.action.IAction;
import net.giantgames.replay.session.object.PacketEntity;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class MetadataAction<E extends PacketEntity> implements IAction<E> {

    private final int index;
    private final Serializable value;

    @Override
    public void apply(int velocity, E object) {
        object.getDataWatcher().setObject(index, value);
        object.updateMetadata();
    }
}
