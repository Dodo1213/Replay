package net.giantgames.replay.serialize;

import net.giantgames.replay.serialize.states.SerializeSignState;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.io.Serializable;

public interface ISerializeBlockState extends Serializable {

    void apply(Block block);

    public static ISerializeBlockState of(Block block) {
        if (block.getState() instanceof Sign) {
            return new SerializeSignState((Sign) block.getState());
        }

        return null;
    }

}
