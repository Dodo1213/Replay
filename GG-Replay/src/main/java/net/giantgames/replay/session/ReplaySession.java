package net.giantgames.replay.session;

import lombok.Getter;
import lombok.Setter;
import net.giantgames.replay.session.recorder.result.ServerRecording;

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
            update();
        }).start();
    }

    public synchronized void update() {
        if (velocity == 0) {
            return;
        }

        recording.play(frame, velocity);

        frame += velocity;
        keep();
    }

    public void shiftBackwards(int units) {
        this.frame -= units;
    }

    public void shiftForward(int units) {
        this.frame += units;
    }

    public void pause(int velocity) {
        this.velocity = 0;
    }

    private synchronized void keep() {
        if (frame >= totalFrames) {
            frame = 0;
        }
        if (frame < 0) {
            frame = totalFrames - 1;
        }
    }

    public synchronized void stop() {
        this.stopRequested = true;
        this.running = false;
    }

}
