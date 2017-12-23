package net.giantgames.replay.serialize;

import com.comphenix.protocol.wrappers.WrappedBlockData;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.io.Serializable;

public class SerializeBlockInfo implements Serializable {

    private ISerializeBlockState state;
    private int id;
    private byte data;

    private SerializeBlockInfo(Block block) {
        this.id = block.getTypeId();
        this.data = block.getData();
        this.state = ISerializeBlockState.of(block);
    }

    public void apply(Vector vector, World world) {
        Block block = world.getBlockAt(vector.toLocation(world));
        block.setTypeIdAndData(id, data, true);

        if (state == null) {
            return;
        }

        state.apply(block);
    }


    public WrappedBlockData convert() {
        return WrappedBlockData.createData(Material.getMaterial(id), data);
    }
}
