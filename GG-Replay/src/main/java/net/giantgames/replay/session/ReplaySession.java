package net.giantgames.replay.session;

import lombok.Getter;
import lombok.Setter;
import net.giantgames.replay.ReplayPlugin;
import net.giantgames.replay.session.object.PacketWorld;
import net.giantgames.replay.session.recorder.result.ServerRecording;

import javax.swing.plaf.synth.SynthTextAreaUI;

@Getter
@Setter
public class ReplaySession {

    private final ServerRecording recording;

    private volatile int frame;
    private volatile int velocity;
    private volatile boolean running;
    private volatile boolean stopRequested;
    private int totalFrames;

    public ReplaySession(ServerRecording recording) {
        this.recording = recording;
        this.totalFrames = recording.length();
        this.frame = 0;
        this.velocity = 1;
        this.running = false;
        this.stopRequested = false;
    }

    public synchronized void run() {
        this.running = true;

        new Thread(() -> {
            PacketWorld packetWorld = new PacketWorld(recording.getBukkitWorld());
            recording.prepare(packetWorld);

            packetWorld.sendAll();

            long lastUpdate = 0;
            for (; ; ) {
                if (System.currentTimeMillis() - lastUpdate > Math.abs(velocity) * ReplayPlugin.UPDATE_INTERVAL) {
                    if (stopRequested) {
                        break;
                    }
                    update();
                    lastUpdate = System.currentTimeMillis();
                }
            }

            packetWorld.removeAll();

        }).start();
    }

    public synchronized void update() {
        if (velocity == 0) {
            return;
        }
        recording.play(frame, velocity);

        frame += velocity > 0 ? 1 : -1;
        keep();
    }

    private synchronized boolean hasValidFrame(int frame) {
        if (frame >= totalFrames) {
            return false;
        }
        if (frame < 0) {
            return false;
        }
        return true;
    }

    public void pause() {
        this.velocity = 0;
    }

    private synchronized void keep() {
        if (frame >= totalFrames) {
            velocity *= -1;
            frame = totalFrames-1;
        }
        if (frame < 0) {
            velocity *= -1;
            frame = 0;
        }
    }

    public synchronized void stop() {
        this.stopRequested = true;
        this.running = false;
        if(ReplayPlugin.getInstance().getCurrentReplaySession().equals(this))
            ReplayPlugin.getInstance().setCurrentReplaySession(null);
    }

    public void forward(int frames) {
        for(; this.frame < this.frame+frames; this.frame ++) {
            System.out.println(this.frame);
            recording.play(this.frame, velocity);
        }
    }

    public void rewind(int frames) {
        for(; this.frame > this.frame-frames; this.frame --) {
            System.out.println(this.frame);
            recording.play(this.frame, -velocity);
        }
    }

}
