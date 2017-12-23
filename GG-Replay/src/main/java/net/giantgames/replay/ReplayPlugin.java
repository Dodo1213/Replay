package net.giantgames.replay;

import lombok.Getter;
import net.giantgames.replay.session.RecordingSession;
import net.giantgames.replay.session.ReplaySession;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class ReplayPlugin extends JavaPlugin {

    @Getter
    private static ReplayPlugin instance;

    private ReplaySession currentReplaySession;
    private RecordingSession currentRecordingSession;

    public ReplayPlugin() {
        instance = this;
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

}
