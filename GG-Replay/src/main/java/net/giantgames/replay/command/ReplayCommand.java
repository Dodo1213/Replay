package net.giantgames.replay.command;

import net.giantgames.replay.ReplayPlugin;
import net.giantgames.replay.io.FileRecordStorage;
import net.giantgames.replay.session.RecordingSession;
import net.giantgames.replay.session.ReplaySession;
import net.giantgames.replay.session.SessionBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ReplayCommand implements CommandExecutor, TabCompleter {

    private final String[] usage;

    public ReplayCommand() {
        this.usage = new String[]{
                "§r",
                ReplayPlugin.PREFIX + "/replay record <id>",
                ReplayPlugin.PREFIX + "/replay finish",
                ReplayPlugin.PREFIX + "/replay play <id>",
                ReplayPlugin.PREFIX + "/replay stop",
                ReplayPlugin.PREFIX + "/replay list",
                "§r"
        };
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            commandSender.sendMessage(usage);
            return true;
        }

        switch (strings[0].toLowerCase()) {
            case "record": {
                if (strings.length < 2) {
                    commandSender.sendMessage(usage[1]);
                    return true;
                }

                if (!(commandSender instanceof Player)) {
                    commandSender.sendMessage(ReplayPlugin.PREFIX + "Du musst ein Spieler sein um eine Aufnahme zu beginnen");
                    return true;
                }

                ReplayPlugin replayPlugin = ReplayPlugin.getInstance();
                if (replayPlugin.getCurrentRecordingSession() != null) {
                    commandSender.sendMessage(ReplayPlugin.PREFIX + "Es läuft bereits eine Aufnahme");
                    return true;
                }

                RecordingSession recordingSession = new SessionBuilder()
                        .inWorld(((Player) commandSender).getWorld())
                        .withGameId(strings[1])
                        .build();

                recordingSession.start();
                replayPlugin.setCurrentRecordingSession(recordingSession);
                commandSender.sendMessage(ReplayPlugin.PREFIX + "Die Aufnahme hat begonnen");
                break;
            }
            case "finish": {
                ReplayPlugin replayPlugin = ReplayPlugin.getInstance();
                if (replayPlugin.getCurrentRecordingSession() == null) {
                    commandSender.sendMessage(ReplayPlugin.PREFIX + "Es läuft momentan keine Aufnahme");
                    return true;
                }
                replayPlugin.getCurrentRecordingSession().stop();
                replayPlugin.setCurrentRecordingSession(null);
                commandSender.sendMessage(ReplayPlugin.PREFIX + "Die Aufnahme wurde beendet.");
                break;
            }
            case "play":
                ReplayPlugin replayPlugin = ReplayPlugin.getInstance();
                if (replayPlugin.getCurrentReplaySession() != null) {
                    commandSender.sendMessage(ReplayPlugin.PREFIX + "Es läuft momentan ein Replay.");
                    return true;
                }
                if (strings.length < 2) {
                    commandSender.sendMessage(usage[1]);
                    return true;
                }

                if (!(commandSender instanceof Player)) {
                    commandSender.sendMessage(ReplayPlugin.PREFIX + "Du musst ein Spieler sein um eine Aufnahme zu beginnen");
                    return true;
                }
                File file = new File("./plugins/Replay/records/"+strings[1]+".rec");
                if(file.exists() == false) {
                    commandSender.sendMessage(ReplayPlugin.PREFIX + "Das Replay konnte nicht gefunden werden.");
                    return true;
                }
                FileRecordStorage recordStorage = new FileRecordStorage(file);
                try {
                    replayPlugin.setCurrentReplaySession(new ReplaySession(recordStorage.importRecord()));
                    replayPlugin.getCurrentReplaySession().run();
                    commandSender.sendMessage(ReplayPlugin.PREFIX + "Das Replay wird gestartet");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case "stop":
                break;
            case "list:":
                break;
            default:
                commandSender.sendMessage(usage);
                return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Arrays.asList("record", "finish", "play", "stop", "list");
    }
}
