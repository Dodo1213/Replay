package net.giantgames.replay.session;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.giantgames.replay.session.recorder.WorldRecorder;
import net.giantgames.replay.session.recorder.result.Recording;
import net.giantgames.replay.session.recorder.result.ServerRecording;

import java.util.Collection;
import java.util.function.Consumer;

@AllArgsConstructor
@Getter
public class RecordingTask implements Runnable {

    private final RecordingSession session;
    private final long period;
    private final SessionProfile profile;
    private final Consumer<ServerRecording> recordingConsumer;

    @Override
    public void run() {
        long lastTick = 0;

        WorldRecorder worldRecorder = new WorldRecorder(session.getPacketWorld());
        int frame = 0;
        for (; ; ) {
            if (System.currentTimeMillis() - lastTick > period) {
                if (session.isStopRequested()) {
                    break;
                }

                tick(frame++, worldRecorder);
                lastTick = System.currentTimeMillis();
            }
        }

        Collection<Recording> recordings = session.finishAll();
        recordings.add(worldRecorder.finish());
        recordingConsumer.accept(new ServerRecording(session.getProfile(), session.getPacketWorld().getWorld().getName(), recordings));
    }

    public void tick(int frame, WorldRecorder worldRecorder) {
        worldRecorder.update(frame);
        session.updateAll(frame);
    }
}
