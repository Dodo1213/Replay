package net.giantgames.replay.io;

import net.giantgames.replay.session.recorder.result.ServerRecording;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface IExporter {

    void export(ServerRecording recording) throws Exception;

}
