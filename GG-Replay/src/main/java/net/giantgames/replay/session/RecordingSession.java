package net.giantgames.replay.session;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import net.giantgames.replay.session.object.PacketEntity;
import net.giantgames.replay.session.object.PacketWorld;
import net.giantgames.replay.session.recorder.AbstractRecorder;
import net.giantgames.replay.session.recorder.EntityRecorder;
import net.giantgames.replay.session.recorder.PlayerRecorder;
import net.giantgames.replay.session.recorder.result.Recording;
import net.giantgames.replay.session.recorder.result.ServerRecording;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;

@Getter
public class RecordingSession {

    private final PacketWorld packetWorld;
    private final Thread thread;
    private volatile boolean stopRequested;
    private volatile boolean running;
    private final Map<UUID, EntityRecorder<?, ?>> recorders;
    private final SessionProfile profile;

    public RecordingSession(SessionProfile profile, World world, long period) {
        this.profile = profile;
        this.packetWorld = new PacketWorld(world);
        this.recorders = Collections.synchronizedMap(new THashMap<>());
        this.thread = new Thread(new RecordingTask(this, period, profile, new Consumer<ServerRecording>() {
            @Override
            public void accept(ServerRecording recording) {
                ReplaySession replaySession = new ReplaySession(recording);
                replaySession.run();
            }
        }));
    }

    public void stopRecord(UUID uuid) {
        AbstractRecorder recorder = recorders.get(uuid);
        if (recorder == null) {
            return;
        }

        recorder.stop();
    }

    public void recordEntity(Entity entity) {
        EntityRecorder<Entity, PacketEntity> entityRecorder = new EntityRecorder<>(entity);
        recorders.put(entity.getUniqueId(), entityRecorder);
    }

    public void record(Player player) {
        PlayerRecorder playerRecorder = new PlayerRecorder(player);
        recorders.put(player.getUniqueId(), playerRecorder);
    }

    public AbstractRecorder getRecorder(UUID uuid) {
        return recorders.get(uuid);
    }

    public void updateAll(int frame) {
        this.recorders.values().forEach((recorder) -> {
            recorder.update(frame);
        });
    }

    public Collection<Recording> finishAll() {
        Collection<Recording> recordings = new LinkedList<>();

        this.recorders.values().forEach((recorder) -> {
            recordings.add(recorder.finish());
        });

        return recordings;
    }

    public synchronized void start() {
        thread.start();
        this.running = true;
    }

    public synchronized void stop() {
        this.stopRequested = true;
    }

}
