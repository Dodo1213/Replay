package net.giantgames.replay.listener.entity;

import net.giantgames.replay.ReplayPlugin;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class EntitySpawnListener implements Listener {

    @EventHandler
    public void onCall(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Item) {
            if (event.isCancelled()) {
                return;
            }

            ReplayPlugin replayPlugin = ReplayPlugin.getInstance();
            if (replayPlugin.getCurrentRecordingSession() == null) {
                return;
            }

            replayPlugin.getCurrentRecordingSession().recordEntity(event.getEntity());
        }
    }

}
