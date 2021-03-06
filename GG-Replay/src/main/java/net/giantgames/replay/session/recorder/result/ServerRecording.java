package net.giantgames.replay.session.recorder.result;

import lombok.Getter;
import net.giantgames.replay.session.SessionProfile;
import net.giantgames.replay.session.object.PacketWorld;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.Serializable;
import java.util.Collection;

@Getter
public class ServerRecording implements Serializable {

    private final SessionProfile profile;
    private Recording[] recordings;
    private int length;
    private final String world;

    public ServerRecording(SessionProfile profile, String world, Collection<Recording> recordingCollection) {
        this.profile = profile;
        this.recordings = recordingCollection.toArray(new Recording[0]);

        for (Recording recording : this.recordings) {
            if (recording.getFrameCount() > length()) {
                length = recording.getFrameCount();
            }
        }

        this.world = world;
    }

    public void prepare(PacketWorld packetWorld) {
        for (Recording recording : recordings) {
            recording.prepare(packetWorld);
        }
    }

    public void play(int frameId, int velocity) {
        for (Recording recording : recordings) {
            recording.play(frameId, velocity);
        }
    }

    public int length() {
        return length;
    }

    public World getBukkitWorld() {
        World world = Bukkit.getWorld(this.world);
        return world == null ? Bukkit.getWorlds().get(0) : world;
    }

}

