package net.giantgames.replay.serialize.states;

import net.giantgames.replay.serialize.ISerializeBlockState;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class SerializeSignState implements ISerializeBlockState {

    private String[] lines;

    public SerializeSignState(Sign sign) {
        this.lines = sign.getLines();
    }

    @Override
    public void apply(Block block) {
        if (!(block.getState() instanceof Sign)) {
            Sign sign = ((Sign) block.getState());
            for (int i = 0; i < 4; i++) {
                if (lines.length > i) {
                    sign.setLine(i, lines[i]);
                } else {
                    sign.setLine(i, "");
                }
            }
        }
    }
}
