package net.giantgames.replay.io;

import net.giantgames.replay.session.recorder.result.ServerRecording;

import java.io.*;

public class FileRecordStorage implements IRecordStorage {

    private final File file;

    public FileRecordStorage(File file) {
        this.file = file;
    }
    
    @Override
    public void export(ServerRecording recording) throws IOException {
        try(ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            oos.writeObject(recording);
            oos.flush();
        }
    }

    @Override
    public ServerRecording importRecord() throws IOException, ClassNotFoundException {
        try(ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            return (ServerRecording) ois.readObject();
        }
    }
}
