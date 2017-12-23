package net.giantgames.replay.session.recorder.result;

import net.giantgames.replay.session.action.IAction;
import net.giantgames.replay.session.frame.Frame;
import net.giantgames.replay.session.object.IReplayObject;

import java.io.Serializable;
import java.util.Collection;

public class ServerRecording implements Serializable {

    private Recording[] recordings;
    private int length;

    public ServerRecording(Collection<Recording> recordingCollection) {
        this.recordings = recordingCollection.toArray(new Recording[0]);

        for (Recording recording : this.recordings) {
            if (recording.getFrameCount() > length()) {
                length = recording.getFrameCount();
            }
        }
    }

    public void prepare() {
        for (Recording recording : recordings) {
            recording.prepare();
        }
    }

    public void play(int frameId, int velocity) {
        for (Recording recording : recordings) {
            for (Frame<IReplayObject> frame : recording.getFrames()) {
                for (IAction<IReplayObject> action : frame.getActions()) {
                    action.apply(velocity, recording.getReplayObject());
                }
            }
        }
    }

    public int length() {
        return length;
    }

}

