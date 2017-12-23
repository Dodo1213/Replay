package net.giantgames.replay.session.recorder;

import lombok.Getter;
import net.giantgames.replay.serialize.SerializeLocation;
import net.giantgames.replay.serialize.SerializeReplayObject;
import net.giantgames.replay.session.action.entity.RemoveAction;
import net.giantgames.replay.session.action.entity.SpawnAction;
import net.giantgames.replay.session.recorder.result.Recording;
import net.giantgames.replay.session.action.entity.MoveAction;
import net.giantgames.replay.session.frame.Frame;
import net.giantgames.replay.session.object.PacketEntity;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

@Getter
public class EntityRecorder<E extends Entity, T extends PacketEntity> extends AbstractRecorder<T> {

    protected static final float MIN_DISTANCE = 0.2f;

    protected E entity;
    protected Location lastLocation;

    public EntityRecorder(E entity) {
        this.entity = entity;
        this.lastLocation = entity.getLocation();
        this.frameBuilder.add(new SpawnAction<>());
    }

    @Override
    public Frame<T> snap(int frame) {

        if (entity.isDead()) {
            return frameBuilder.buildAndClear();
        }

        Location location = entity.getLocation();
        if (lastLocation != null) {
            if (location.distanceSquared(lastLocation) > MIN_DISTANCE || location.getYaw() != lastLocation.getYaw() || location.getPitch() != lastLocation.getPitch()) {
                frameBuilder.add(new MoveAction<T>(SerializeLocation.from(lastLocation), SerializeLocation.from(location)));
            }
        }

        lastLocation = location;
        return frameBuilder.buildAndClear();
    }

    @Override
    public Recording finish() {
        return new Recording(startFrame, new SerializeReplayObject(entity.getUniqueId().toString(), entity.getType().getTypeId(), null), frames);
    }

    @Override
    public synchronized void stop() {
        this.frameBuilder.add(new RemoveAction<>());
        super.stop();
    }
}
