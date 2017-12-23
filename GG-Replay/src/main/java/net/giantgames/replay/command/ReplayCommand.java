package net.giantgames.replay.command;

import net.giantgames.replay.ReplayPlugin;
import net.giantgames.replay.session.RecordingSession;
import net.giantgames.replay.session.SessionBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

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
