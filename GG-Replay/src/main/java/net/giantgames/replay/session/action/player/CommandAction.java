package net.giantgames.replay.session.action.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.giantgames.replay.session.action.IAction;
import net.giantgames.replay.session.object.PacketPlayer;
import org.bukkit.Bukkit;

@Getter
@AllArgsConstructor
public class CommandAction implements IAction<PacketPlayer> {

    private static final String FORMAT = "§3>> §e%s §7executed : §b%s";

    private final String command;

    @Override
    public void apply(int velocity, PacketPlayer object) {
        Bukkit.broadcastMessage(String.format(FORMAT, object.getProfile().getName(), command));
    }

}
