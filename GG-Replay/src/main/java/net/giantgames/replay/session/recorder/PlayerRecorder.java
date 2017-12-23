package net.giantgames.replay.session.recorder;

import static net.giantgames.replay.session.object.PacketEntity.Slot.*;

import lombok.Getter;
import lombok.Setter;
import net.giantgames.replay.serialize.SerializeGameProfile;
import net.giantgames.replay.serialize.SerializeReplayObject;
import net.giantgames.replay.session.recorder.result.Recording;
import net.giantgames.replay.session.action.entity.EquipAction;
import net.giantgames.replay.session.frame.Frame;
import net.giantgames.replay.session.object.PacketPlayer;
import org.bukkit.entity.Player;

@Getter
@Setter
public class PlayerRecorder extends EntityRecorder<Player, PacketPlayer> {

    public PlayerRecorder(Player player) {
        super(player);

        frameBuilder.add(new EquipAction(HAND, null, player.getItemInHand()))
                .add(new EquipAction(HELMET, null, player.getInventory().getHelmet()))
                .add(new EquipAction(CHESTPLATE, null, player.getInventory().getChestplate()))
                .add(new EquipAction(LEGGINGS, null, player.getInventory().getLeggings()))
                .add(new EquipAction(BOOTS, null, player.getInventory().getBoots()));
    }

    @Override
    public Frame<PacketPlayer> snap(int frame) {
        if (!entity.isOnline()) {
            return frameBuilder.buildAndClear();
        }

        return super.snap(frame);
    }

    @Override
    public Recording finish() {
        return new Recording(startFrame, new SerializeReplayObject(entity.getUniqueId().toString(), entity.getType().getTypeId(), new SerializeGameProfile(entity)), frames);
    }
}
