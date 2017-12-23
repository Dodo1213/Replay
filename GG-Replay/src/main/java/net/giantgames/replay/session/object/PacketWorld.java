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
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import gnu.trove.map.hash.THashMap;
import lombok.Getter;
import net.giantgames.replay.serialize.SerializeBlockChange;
import net.giantgames.replay.session.frame.Frame;
import net.giantgames.replay.session.frame.FrameBuilder;
import net.giantgames.replay.session.recorder.EntityRecorder;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class PacketWorld implements IReplayObject {

    private final World world;
    private final Multimap<ChunkCoordIntPair, SerializeBlockChange> blockChanges;
    private final Map<PacketEntity, EntityRecorder<? extends Entity, ? extends PacketEntity>> spawnedEntities;
    private final Collection<PacketPlayer> packetPlayers;

    public PacketWorld(World world) {
        this.blockChanges = Multimaps.synchronizedMultimap(HashMultimap.create());
        this.spawnedEntities = Collections.synchronizedMap(new THashMap<>());
        this.packetPlayers = Collections.synchronizedList(new LinkedList<>());
        this.world = world;
    }


    public void snap(FrameBuilder<IReplayObject> frameBuilder) {
        for (EntityRecorder<? extends Entity, ? extends PacketEntity> entityRecorder : spawnedEntities.values()) {
            frameBuilder.add(entityRecorder.snap());
        }
    }

    public void remove(PacketEntity packetEntity) {
        if (spawnedEntities.containsKey(packetEntity)) {
            spawnedEntities.remove(packetEntity);
        }
    }

    public <E extends Entity, T extends PacketEntity> void record(T packetEntity, EntityRecorder<E, T> recorder) {
        if (!spawnedEntities.containsKey(packetEntity)) {
            spawnedEntities.put(packetEntity, recorder);
        }

        if (packetEntity instanceof PacketPlayer) {
            if (!packetPlayers.contains(packetEntity)) {
                packetPlayers.add((PacketPlayer) packetEntity);
            }
        }
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
        PacketContainer[] multiBlockChanges = new PacketContainer[blockChanges.size()];

        int i = 0;
        for (ChunkCoordIntPair chunkCoordIntPair : blockChanges.keys()) {

            WrapperPlayServerMultiBlockChange multiBlockChange = new WrapperPlayServerMultiBlockChange();
            multiBlockChange.setRecords(blockChanges.get(chunkCoordIntPair).toArray(new MultiBlockChangeInfo[0]));
            multiBlockChange.setChunk(chunkCoordIntPair);

            multiBlockChanges[i++] = multiBlockChange.getHandle();
        }

        return multiBlockChanges;
    }

    @Override
    public PacketContainer[] remove() {
        PacketContainer[] multiBlockChanges = new PacketContainer[blockChanges.size()];

        int i = 0;
        for (ChunkCoordIntPair chunkCoordIntPair : blockChanges.keys()) {

            WrapperPlayServerMultiBlockChange multiBlockChange = new WrapperPlayServerMultiBlockChange();
            Collection<SerializeBlockChange> serializeBlockChanges = blockChanges.get(chunkCoordIntPair);
            MultiBlockChangeInfo[] multiBlockChangeInfos = new MultiBlockChangeInfo[serializeBlockChanges.size()];

            int j = 0;
            for (SerializeBlockChange serializeBlockChange : serializeBlockChanges) {
                multiBlockChangeInfos[j++] = getRealBlock(serializeBlockChange.getVector().convert().toLocation(world));
            }

            multiBlockChange.setRecords(multiBlockChangeInfos);
            multiBlockChange.setChunk(chunkCoordIntPair);

            multiBlockChanges[i++] = multiBlockChange.getHandle();
        }

        return multiBlockChanges;
    }

    public MultiBlockChangeInfo getRealBlock(Location location) {
        Block block = location.getBlock();
        return new MultiBlockChangeInfo(location, WrappedBlockData.createData(block.getType(), block.getTypeId()));
    }
}
