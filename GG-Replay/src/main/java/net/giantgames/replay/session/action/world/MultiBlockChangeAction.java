package net.giantgames.replay.session.action.world;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.giantgames.replay.serialize.SerializeBlockChange;
import net.giantgames.replay.session.action.IAction;
import net.giantgames.replay.session.object.PacketWorld;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@Getter
public class MultiBlockChangeAction implements IAction<PacketWorld> {

    private final Collection<SerializeBlockChange> blockChanges;

    @Override
    public void apply(int velocity, PacketWorld object) {
        object.multiBlockChange(blockChanges, velocity);
    }
}
