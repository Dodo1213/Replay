package net.giantgames.replay.session.action.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.giantgames.replay.session.action.IAction;
import net.giantgames.replay.session.object.PacketEntity;

import java.io.Serializable;

@AllArgsConstructor
public class MetadataAction<E extends PacketEntity> implements IAction<E> {

    private final Type type;
    private final boolean flag;

    @Override
    public void apply(int velocity, E object) {
        byte id = 0;
        boolean ourFlag = flag;
        if(velocity < 0) ourFlag = !ourFlag;
        if (ourFlag) {
            object.getDataWatcher().setObject(id, (byte) (id | 1 << type.getIndex()));
        } else {
            object.getDataWatcher().setObject(id, (byte) (id & ~(1 << type.getIndex())));
        }
        object.updateMetadata();
    }

    @Getter
    @AllArgsConstructor
    public enum Type implements Serializable {

        SNEAK(1),
        SPRINT(3);

        private final int index;
    }

}
