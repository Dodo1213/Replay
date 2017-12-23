package net.giantgames.replay.session;

import lombok.Getter;
import net.giantgames.replay.session.object.PacketWorld;
import org.bukkit.World;

@Getter
public class RecordingSession {

    private final PacketWorld packetWorld;
    private final Thread thread;
    private volatile boolean stopRequested;
    private volatile boolean running;

    public RecordingSession(World world, long period) {
        this.packetWorld = new PacketWorld(world);
        this.thread = new Thread(new RecordingTask(this, period, (recording) -> {

            ReplaySession replaySession = new ReplaySession(recording);
            replaySession.run();

        }));
    }

    public synchronized void start() {
        thread.start();
        this.running = true;
    }

    public synchronized void stop() {
        this.stopRequested = true;
    }

}
