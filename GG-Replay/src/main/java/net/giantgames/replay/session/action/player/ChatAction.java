package net.giantgames.replay.session.action.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.giantgames.replay.session.action.IAction;
import net.giantgames.replay.session.object.PacketPlayer;
import org.bukkit.Bukkit;

@AllArgsConstructor
@Getter
public class ChatAction implements IAction<PacketPlayer> {

    private static final String FORMAT = "§3>> §e%s §7wrote : §b%s";

    private final String chat;

    @Override
    public void apply(int velocity, PacketPlayer object) {
        Bukkit.broadcastMessage(String.format(FORMAT, object.getProfile().getName(), chat));
    }

}
