package net.giantgames.replay.listener.player;

import net.giantgames.replay.ReplayPlugin;
import net.giantgames.replay.session.action.player.MetadataAction;
import net.giantgames.replay.session.recorder.AbstractRecorder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class PlayerToggleSneakListener implements Listener {

    @EventHandler
    public void onCall(PlayerToggleSneakEvent event) {

        if (event.isCancelled()) {
            return;
        }

        ReplayPlugin replayPlugin = ReplayPlugin.getInstance();
        if (replayPlugin.getCurrentRecordingSession() == null) {
            return;
        }

        AbstractRecorder recorder = replayPlugin.getCurrentRecordingSession().getRecorder(event.getPlayer().getUniqueId());
        if (recorder != null) {
            recorder.getFrameBuilder().add(new MetadataAction(MetadataAction.Type.SNEAK, event.isSneaking()));
        }

    }

}