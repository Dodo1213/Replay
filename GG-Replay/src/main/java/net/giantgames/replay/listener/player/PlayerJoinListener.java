package net.giantgames.replay.listener.player;

import net.giantgames.replay.ReplayPlugin;
import net.giantgames.replay.session.recorder.AbstractRecorder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    private void onCall(PlayerJoinEvent event) {
        ReplayPlugin replayPlugin = ReplayPlugin.getInstance();
        if (replayPlugin.getCurrentRecordingSession() == null) {
            return;
        }

        replayPlugin.getCurrentRecordingSession().record(event.getPlayer());
    }


}
