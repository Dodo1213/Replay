package net.giantgames.replay.session.action.world;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.giantgames.replay.serialize.SerializeBlockChange;
import net.giantgames.replay.session.action.IAction;
import net.giantgames.replay.session.object.PacketWorld;

@AllArgsConstructor
@Getter
public class BlockChangeAction implements IAction<PacketWorld> {

    private final SerializeBlockChange blockChange;

    @Override
    public void apply(int velocity, PacketWorld object) {
        object.blockChange(blockChange, velocity);
    }
}
