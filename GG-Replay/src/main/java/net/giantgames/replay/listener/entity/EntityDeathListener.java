package net.giantgames.replay.listener.entity;

import net.giantgames.replay.ReplayPlugin;
import net.giantgames.replay.session.action.entity.StatusAction;
import net.giantgames.replay.session.object.PacketEntity;
import net.giantgames.replay.session.recorder.AbstractRecorder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCall(EntityDeathEvent event) {

        ReplayPlugin replayPlugin = ReplayPlugin.getInstance();
        if (replayPlugin.getCurrentRecordingSession() == null) {
            return;
        }

        if(!event.getEntity().getWorld().getUID().equals(replayPlugin.getCurrentRecordingSession().getPacketWorld().getWorld().getUID())) {
            return;
        }

        AbstractRecorder recorder = replayPlugin.getCurrentRecordingSession().getRecorder(event.getEntity().getUniqueId());
        if (recorder != null) {
            recorder.getFrameBuilder().add(new StatusAction(PacketEntity.Status.DEATH));
        }
    }

}
