package net.giantgames.replay.io;

import net.giantgames.replay.session.recorder.result.ServerRecording;

import java.io.IOException;

public interface IImporter {

    ServerRecording importRecord() throws Exception;

}
