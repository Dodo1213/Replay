package net.giantgames.replay;

import lombok.Getter;
import lombok.Setter;
import net.giantgames.replay.command.ReplayCommand;
import net.giantgames.replay.session.RecordingSession;
import net.giantgames.replay.session.ReplaySession;
import net.giantgames.replay.util.Classes;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@Setter
public class ReplayPlugin extends JavaPlugin {

    public static final long UPDATE_INTERVAL = 100;
    public static final String PREFIX = "§b§lGiant§5§lEye §8» §7";
    @Getter
    private static ReplayPlugin instance;

    private ReplaySession currentReplaySession;
    private RecordingSession currentRecordingSession;

    public ReplayPlugin() {
        instance = this;

        this.currentRecordingSession = null;
        this.currentReplaySession = null;
    }

    @Override
    public void onEnable() {
        getCommand("replay").setExecutor(new ReplayCommand());

        Classes.forEach("net.giantgames.listener", Listener.class, (listener) -> {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }, "player entity".split(" "), false);
    }

    @Override
    public void onDisable() {
    }


}
