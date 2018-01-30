package net.giantgames.replay.io;

import lombok.Getter;
import net.giantgames.replay.session.recorder.result.ServerRecording;

import java.io.*;
import java.util.Base64;

public class Base64RecordStorage implements IRecordStorage {

    @Getter
    private String string;

    public Base64RecordStorage(String input) {
        this.string = input;
    }

    public Base64RecordStorage() {}

    @Override
    public void export(ServerRecording recording) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(bos));
        oos.writeObject(recording);
        oos.flush();
        oos.close();
        string = Base64.getEncoder().encodeToString(bos.toByteArray());
    }

    @Override
    public ServerRecording importRecord() throws Exception {
        ByteArrayInputStream bis = new ByteArrayInputStream(Base64.getDecoder().decode(string));
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(bis));
        ServerRecording out = (ServerRecording) ois.readObject();
        ois.close();
        return out;
    }
}
