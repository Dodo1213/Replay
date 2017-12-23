package net.giantgames.replay.session.action.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.giantgames.replay.session.action.IAction;
import net.giantgames.replay.session.object.PacketEntity;

@AllArgsConstructor
@Getter
public class AnimationAction<E extends PacketEntity> implements IAction<E> {

    private final PacketEntity.Animation animation;


    @Override
    public void apply(int velocity, E object) {
        object.animate(animation);
    }
}
