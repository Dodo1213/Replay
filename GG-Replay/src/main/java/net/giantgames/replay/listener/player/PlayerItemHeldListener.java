package net.giantgames.replay.listener.player;

import net.giantgames.replay.ReplayPlugin;
import net.giantgames.replay.serialize.SerializeLocation;
import net.giantgames.replay.session.action.entity.EquipAction;
import net.giantgames.replay.session.action.entity.MoveAction;
import net.giantgames.replay.session.object.PacketEntity;
import net.giantgames.replay.session.recorder.AbstractRecorder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerItemHeldListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCall(PlayerItemHeldEvent event) {
        if (event.isCancelled()) {
            return;
        }

        ReplayPlugin replayPlugin = ReplayPlugin.getInstance();
        if (replayPlugin.getCurrentRecordingSession() == null) {
            return;
        }

        AbstractRecorder recorder = replayPlugin.getCurrentRecordingSession().getRecorder(event.getPlayer().getUniqueId());
        if (recorder != null) {
            recorder.getFrameBuilder().add(new EquipAction(PacketEntity.Slot.HAND,
                    event.getPlayer().getInventory().getItem(event.getPreviousSlot()),
                    event.getPlayer().getInventory().getItem(event.getNewSlot())));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMove(PlayerMoveEvent e) {
        if(e.isCancelled()) {
            return;
        }
        ReplayPlugin replayPlugin = ReplayPlugin.getInstance();
        if (replayPlugin.getCurrentRecordingSession() == null) {
            return;
        }

        AbstractRecorder recorder = replayPlugin.getCurrentRecordingSession().getRecorder(e.getPlayer().getUniqueId());
        if (recorder != null) {
            recorder.getFrameBuilder().add(new MoveAction(SerializeLocation.from(e.getFrom()), SerializeLocation.from(e.getTo())));
        }
    }

}
