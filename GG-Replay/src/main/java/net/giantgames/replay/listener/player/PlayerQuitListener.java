package net.giantgames.replay.listener.player;

import net.giantgames.replay.ReplayPlugin;
import net.giantgames.replay.session.action.entity.RemoveAction;
import net.giantgames.replay.session.recorder.AbstractRecorder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    private void onCall(PlayerQuitEvent event) {

        ReplayPlugin replayPlugin = ReplayPlugin.getInstance();
        if (replayPlugin.getCurrentRecordingSession() == null) {
            return;
        }

        AbstractRecorder recorder = replayPlugin.getCurrentRecordingSession().getRecorder(event.getPlayer().getUniqueId());
        if (recorder != null) {
            recorder.getFrameBuilder().add(new RemoveAction());
        }

    }

}
