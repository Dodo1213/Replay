package net.giantgames.replay.session;

import net.giantgames.replay.ReplayPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class SessionBuilder {

    private World world;
    private String gameId, game;
    private long date, interval;

    public SessionBuilder() {
        this.world = Bukkit.getWorlds().get(0);
        this.gameId = String.format("#%5d", ((int) Math.random() * 1000));
        this.game = "Unknown";
        this.date = System.currentTimeMillis();
        this.interval = ReplayPlugin.UPDATE_INTERVAL;
    }

    public SessionBuilder inWorld(World world) {
        this.world = world;
        return this;
    }

    public SessionBuilder withGameId(String gameId) {
        this.game = gameId;
        return this;
    }

    public SessionBuilder withGame(String game) {
        this.game = game;
        return this;
    }

    public long withDate(long date) {
        this.date = date;
        return this.date;
    }

    public long withInterval(long interval) {
        this.interval = interval;
        return this.interval;
    }

    public RecordingSession build() {
        return new RecordingSession(new SessionProfile(gameId, game, date), world, interval);
    }

}
