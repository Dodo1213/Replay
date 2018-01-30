package net.giantgames.replay;

import com.comphenix.packetwrapper.WrapperHandshakingClientSetProtocol;
import com.comphenix.packetwrapper.WrapperLoginClientStart;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.netty.ProtocolInjector;
import lombok.Getter;
import lombok.Setter;
import net.giantgames.replay.command.ReplayCommand;
import net.giantgames.replay.listener.player.PlayerJoinListener;
import net.giantgames.replay.listener.player.PlayerQuitListener;
import net.giantgames.replay.session.RecordingSession;
import net.giantgames.replay.session.ReplaySession;
import net.giantgames.replay.util.Classes;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

@Getter
@Setter
public class ReplayPlugin extends JavaPlugin implements Listener {

    public static final long UPDATE_INTERVAL = 50;
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
        Bukkit.getPluginManager().registerEvents(this, this);
        Classes.forEach("net.giantgames.replay.listener", Listener.class, (listener) -> {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }, "player entity block".split(" "), false);
    }

    @Override
    public void onDisable() {
    }
}
