package net.giantgames.replay.session.object;

import com.comphenix.packetwrapper.WrapperPlayServerBlockChange;
import com.comphenix.packetwrapper.WrapperPlayServerMultiBlockChange;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.ChunkCoordIntPair;
import com.comphenix.protocol.wrappers.MultiBlockChangeInfo;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import lombok.Getter;
import net.giantgames.replay.serialize.SerializeBlockChange;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

@Getter
public class PacketWorld implements IReplayObject {

    private final World world;
    private final Multimap<ChunkCoordIntPair, SerializeBlockChange> blockChanges;
    private final Collection<PacketEntity> spawnedEntities;
    private final Collection<PacketPlayer> packetPlayers;

    public PacketWorld(World world) {
        this.blockChanges = Multimaps.synchronizedMultimap(HashMultimap.create());
        this.spawnedEntities = Collections.synchronizedList(new LinkedList<>());
        this.packetPlayers = Collections.synchronizedList(new LinkedList<>());
        this.world = world;
    }

    public void add(PacketEntity packetEntity) {
        if (packetEntity instanceof PacketPlayer) {
            this.packetPlayers.add((PacketPlayer) packetEntity);
        }

        this.spawnedEntities.add(packetEntity);
    }

    public void remove(PacketEntity packetEntity) {
        spawnedEntities.remove(packetEntity);
        packetPlayers.remove(packetEntity);
    }

    public void multiBlockChange(Collection<SerializeBlockChange> changes, int velocity) {
        Multimap<ChunkCoordIntPair, MultiBlockChangeInfo> datas = HashMultimap.create();

        for (SerializeBlockChange blockChange : changes) {
            Location location = blockChange.getVector().convert().toLocation(world);
            Chunk chunk = location.getChunk();
            ChunkCoordIntPair chunkCoordIntPair = new ChunkCoordIntPair(chunk.getX(), chunk.getZ());
            this.blockChanges.put(chunkCoordIntPair, blockChange);
            datas.put(chunkCoordIntPair, new MultiBlockChangeInfo(location, velocity > 0 ? blockChange.getTo().convert() : blockChange.getFrom().convert()));
        }

        for (ChunkCoordIntPair chunkCoordIntPair : datas.keys()) {

            WrapperPlayServerMultiBlockChange multiBlockChange = new WrapperPlayServerMultiBlockChange();
            multiBlockChange.setRecords(datas.get(chunkCoordIntPair).toArray(new MultiBlockChangeInfo[0]));
            multiBlockChange.setChunk(chunkCoordIntPair);

            ProtocolLibrary.getProtocolManager().broadcastServerPacket(multiBlockChange.getHandle());
        }
    }

    public void blockChange(SerializeBlockChange blockChange, int velocity) {

        Location location = blockChange.getVector().convert().toLocation(world);
        Chunk chunk = location.getChunk();

        this.blockChanges.put(new ChunkCoordIntPair(chunk.getX(), chunk.getZ()), blockChange);

        WrapperPlayServerBlockChange playServerBlockChange = new WrapperPlayServerBlockChange();
        playServerBlockChange.setBlockData(velocity > 0 ? blockChange.getTo().convert() : blockChange.getFrom().convert());
        playServerBlockChange.setLocation(new BlockPosition(location.toVector()));

        ProtocolLibrary.getProtocolManager().broadcastServerPacket(playServerBlockChange.getHandle());
    }

    @Override
    public PacketContainer[] send() {
        LinkedList<PacketContainer> containers = new LinkedList<>();

        for (ChunkCoordIntPair chunkCoordIntPair : blockChanges.keys()) {

            WrapperPlayServerMultiBlockChange multiBlockChange = new WrapperPlayServerMultiBlockChange();
            multiBlockChange.setRecords(blockChanges.get(chunkCoordIntPair).toArray(new MultiBlockChangeInfo[0]));
            multiBlockChange.setChunk(chunkCoordIntPair);

            containers.add(multiBlockChange.getHandle());
        }

        spawnedEntities.forEach(spawnedEntities -> {
            PacketContainer[] entityContainer = spawnedEntities.send();
            for (int j = 0; j < entityContainer.length; j++) {
                containers.add(entityContainer[j]);
            }
        });

        return containers.toArray(new PacketContainer[0]);
    }

    @Override
    public PacketContainer[] remove() {
        LinkedList<PacketContainer> containers = new LinkedList<>();

        for (ChunkCoordIntPair chunkCoordIntPair : blockChanges.keys()) {

            WrapperPlayServerMultiBlockChange multiBlockChange = new WrapperPlayServerMultiBlockChange();
            Collection<SerializeBlockChange> serializeBlockChanges = blockChanges.get(chunkCoordIntPair);
            MultiBlockChangeInfo[] multiBlockChangeInfos = new MultiBlockChangeInfo[serializeBlockChanges.size()];

            int i = 0;
            for (SerializeBlockChange serializeBlockChange : serializeBlockChanges) {
                multiBlockChangeInfos[i++] = getRealBlock(serializeBlockChange.getVector().convert().toLocation(world));
            }

            multiBlockChange.setRecords(multiBlockChangeInfos);
            multiBlockChange.setChunk(chunkCoordIntPair);

            containers.add(multiBlockChange.getHandle());
        }

        spawnedEntities.forEach(spawnedEntities -> {
            PacketContainer[] entityContainer = spawnedEntities.remove();
            for (int j = 0; j < entityContainer.length; j++) {
                containers.add(entityContainer[j]);
            }
        });

        return containers.toArray(new PacketContainer[0]);
    }

    public MultiBlockChangeInfo getRealBlock(Location location) {
        Block block = location.getBlock();
        return new MultiBlockChangeInfo(location, WrappedBlockData.createData(block.getType(), block.getTypeId()));
    }
}
