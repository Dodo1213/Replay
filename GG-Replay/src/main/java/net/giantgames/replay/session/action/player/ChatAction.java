package net.giantgames.replay.session.action.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.giantgames.replay.session.action.IAction;
import net.giantgames.replay.session.object.PacketPlayer;
import org.bukkit.Bukkit;

@AllArgsConstructor
@Getter
public class ChatAction implements IAction<PacketPlayer> {

    private final String chat;

    @Override
    public void apply(int velocity, PacketPlayer object) {
        Bukkit.broadcastMessage(chat);
    }

}
