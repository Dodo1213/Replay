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
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import net.giantgames.replay.serialize.SerializeBlockChange;
import net.giantgames.replay.session.recorder.result.Recording;
import net.giantgames.replay.session.recorder.EntityRecorder;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.*;

@Getter
public class PacketWorld implements IReplayObject {

    private final World world;
    private final Multimap<ChunkCoordIntPair, SerializeBlockChange> blockChanges;
    private final Map<PacketEntity, EntityRecorder<? extends Entity, ? extends PacketEntity>> spawnedEntities;
    private final Collection<PacketPlayer> packetPlayers;

    public PacketWorld(World world) {
        this.blockChanges = Multimaps.synchronizedMultimap(HashMultimap.create());
        this.spawnedEntities = Collections.synchronizedMap(new Object2ObjectOpenHashMap());
        this.packetPlayers = Collections.synchronizedList(new LinkedList<>());
        this.world = world;
    }

    public Collection<Recording> finishAll() {
        Collection<Recording> recordings = new LinkedList<>();
        spawnedEntities.values().forEach((entities) -> {
            recordings.add(entities.finish());
        });
        return recordings;
    }

    public void updateAll(int frame) {
        spawnedEntities.values().forEach((entities) -> {
            entities.update(frame);
        });
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
        LinkedList<PacketContainer> containers = new LinkedList<>();

        for (ChunkCoordIntPair chunkCoordIntPair : blockChanges.keys()) {

            WrapperPlayServerMultiBlockChange multiBlockChange = new WrapperPlayServerMultiBlockChange();
            multiBlockChange.setRecords(blockChanges.get(chunkCoordIntPair).toArray(new MultiBlockChangeInfo[0]));
            multiBlockChange.setChunk(chunkCoordIntPair);

            containers.add(multiBlockChange.getHandle());
        }

        spawnedEntities.keySet().forEach(spawnedEntities -> {
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

        spawnedEntities.keySet().forEach(spawnedEntities -> {
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
