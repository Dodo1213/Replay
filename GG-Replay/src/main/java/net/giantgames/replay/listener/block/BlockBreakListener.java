package net.giantgames.replay.listener.block;

import net.giantgames.replay.ReplayPlugin;
import net.giantgames.replay.serialize.SerializeBlockChange;
import net.giantgames.replay.session.action.player.MetadataAction;
import net.giantgames.replay.session.action.world.BlockChangeAction;
import net.giantgames.replay.session.recorder.AbstractRecorder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCall(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }

        ReplayPlugin replayPlugin = ReplayPlugin.getInstance();
        if (replayPlugin.getCurrentRecordingSession() == null) {
            return;
        }

        if(!event.getBlock().getWorld().getUID().equals(replayPlugin.getCurrentRecordingSession().getPacketWorld().getWorld().getUID())) {
            return;
        }

        AbstractRecorder recorder = replayPlugin.getCurrentRecordingSession().getWorldRecorder();
        if (recorder != null) {
            recorder.getFrameBuilder().add(new BlockChangeAction(new SerializeBlockChange(event.getBlock(), null)));
        }

    }

}
