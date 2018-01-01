package net.giantgames.replay.session.recorder;

import static net.giantgames.replay.session.object.PacketEntity.Slot.*;

import lombok.Getter;
import lombok.Setter;
import net.giantgames.replay.serialize.SerializeGameProfile;
import net.giantgames.replay.serialize.SerializeReplayObject;
import net.giantgames.replay.session.object.PacketEntity;
import net.giantgames.replay.session.recorder.result.Recording;
import net.giantgames.replay.session.action.entity.EquipAction;
import net.giantgames.replay.session.frame.Frame;
import net.giantgames.replay.session.object.PacketPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class PlayerRecorder extends EntityRecorder<Player, PacketPlayer> {

    private ItemStack[] lastContents;

    public PlayerRecorder(Player player) {
        super(player);

        frameBuilder.add(new EquipAction(HAND, null, player.getItemInHand()))
                .add(new EquipAction(HELMET, null, player.getInventory().getHelmet()))
                .add(new EquipAction(CHESTPLATE, null, player.getInventory().getChestplate()))
                .add(new EquipAction(LEGGINGS, null, player.getInventory().getLeggings()))
                .add(new EquipAction(BOOTS, null, player.getInventory().getBoots()));
        this.lastContents = player.getInventory().getArmorContents();
    }

    @Override
    public Frame<PacketPlayer> snap(int frame) {
        if (!entity.isOnline()) {
            return frameBuilder.buildAndClear();
        }

        for (int i = 0; i < lastContents.length; i++) {
            boolean changed = false;
            ItemStack last = lastContents[i];
            ItemStack now = entity.getInventory().getArmorContents()[i];
            if (last != null && now != null) {
                if (!last.equals(now)) {
                    changed = true;
                }
            } else {
                changed = (last != null) != (now != null);
            }

            if (changed) {
                frameBuilder.add(new EquipAction<>(PacketEntity.Slot.fromArmorSlot(i), last, now));
            }
        }

        return super.snap(frame);
    }

    @Override
    public Recording finish() {
        return new Recording(startFrame, new SerializeReplayObject(entity.getUniqueId().toString(), 10, new SerializeGameProfile(entity)), frames);
    }
}
