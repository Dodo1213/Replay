package net.giantgames.replay.listener.player;

import net.giantgames.replay.ReplayPlugin;
import net.giantgames.replay.session.action.entity.AnimationAction;
import net.giantgames.replay.session.action.player.ChatAction;
import net.giantgames.replay.session.object.PacketEntity;
import net.giantgames.replay.session.recorder.AbstractRecorder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;

public class PlayerAnimationListener implements Listener {

    @EventHandler
    public void onCall(PlayerAnimationEvent event) {

        if (event.isCancelled()) {
            return;
        }

        ReplayPlugin replayPlugin = ReplayPlugin.getInstance();
        if (replayPlugin.getCurrentRecordingSession() == null) {
            return;
        }

        AbstractRecorder recorder = replayPlugin.getCurrentRecordingSession().getRecorder(event.getPlayer().getUniqueId());
        if (recorder != null) {
            recorder.getFrameBuilder().add(new AnimationAction(PacketEntity.Animation.SWING_MAIN_ARM));
        }

    }

}
