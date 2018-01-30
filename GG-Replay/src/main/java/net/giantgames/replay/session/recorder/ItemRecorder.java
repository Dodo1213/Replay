package net.giantgames.replay.session.recorder;

import net.giantgames.replay.serialize.SerializeItemStack;
import net.giantgames.replay.serialize.SerializeReplayObject;
import net.giantgames.replay.session.object.PacketItem;
import net.giantgames.replay.session.recorder.result.Recording;
import org.bukkit.entity.Item;

public class ItemRecorder extends EntityRecorder<Item, PacketItem> {

    public ItemRecorder(Item entity) {
        super(entity);
    }

    @Override
    public Recording finish() {
        return new Recording(startFrame, new SerializeReplayObject(entity.getUniqueId().toString(), 1, null, SerializeItemStack.from(entity.getItemStack())), frames);
    }
}
