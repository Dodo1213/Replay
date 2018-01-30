package net.giantgames.replay.session.object;

import com.comphenix.protocol.PacketType;
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
import lombok.RequiredArgsConstructor;
import net.giantgames.replay.ReplayPlugin;
import net.giantgames.replay.serialize.SerializeBlockChange;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
            PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.MULTI_BLOCK_CHANGE);
            packetContainer.getMultiBlockChangeInfoArrays().write(0, datas.get(chunkCoordIntPair).toArray(new MultiBlockChangeInfo[0]));
            packetContainer.getChunkCoordIntPairs().write(0, chunkCoordIntPair);

            ProtocolLibrary.getProtocolManager().broadcastServerPacket(packetContainer);
        }
    }

    public void blockChange(SerializeBlockChange blockChange, int velocity) {
        Bukkit.getScheduler().runTask(ReplayPlugin.getInstance(), () -> {
            Location location = blockChange.getVector().convert().toLocation(world);
            Chunk chunk = location.getChunk();
            this.blockChanges.put(new ChunkCoordIntPair(chunk.getX(), chunk.getZ()), blockChange);
            PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
            packetContainer.getBlockData().write(0, velocity > 0 ? blockChange.getTo().convert() : blockChange.getFrom().convert());
            packetContainer.getBlockPositionModifier().write(0, new BlockPosition(location.toVector()));
            ProtocolLibrary.getProtocolManager().broadcastServerPacket(packetContainer);
        });
    }

    @Override
    public PacketContainer[] send() {
        LinkedList<PacketContainer> containers = new LinkedList<>();

        for (ChunkCoordIntPair chunkCoordIntPair : blockChanges.keys()) {
            PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.MULTI_BLOCK_CHANGE);
            packetContainer.getMultiBlockChangeInfoArrays().write(0, blockChanges.get(chunkCoordIntPair).toArray(new MultiBlockChangeInfo[0]));
            packetContainer.getChunkCoordIntPairs().write(0, chunkCoordIntPair);

            ProtocolLibrary.getProtocolManager().broadcastServerPacket(packetContainer);
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

            PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.MULTI_BLOCK_CHANGE);
            Collection<SerializeBlockChange> serializeBlockChanges = blockChanges.get(chunkCoordIntPair);

            MultiBlockChangeInfo[] multiBlockChangeInfos = new MultiBlockChangeInfo[serializeBlockChanges.size()];

            int i = 0;
            for (SerializeBlockChange serializeBlockChange : serializeBlockChanges) {
                multiBlockChangeInfos[i++] = getRealBlock(serializeBlockChange.getVector().convert().toLocation(world));
            }

            packetContainer.getMultiBlockChangeInfoArrays().write(0, multiBlockChangeInfos);
            packetContainer.getChunkCoordIntPairs().write(0, chunkCoordIntPair);

            containers.add(packetContainer);
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
        Block block = new RealBlockFuture(location).getBlock();
        return new MultiBlockChangeInfo(location, WrappedBlockData.createData(block.getType(), block.getTypeId()));
    }

    @RequiredArgsConstructor
    private class RealBlockFuture implements Future<Block> {

        private Block block;
        private final Location location;

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return block != null;
        }

        @Override
        public Block get() throws InterruptedException, ExecutionException {
            Bukkit.getScheduler().runTask(ReplayPlugin.getInstance(), () -> {
                block = location.getBlock();
            });
            while (!isDone()) {
                Thread.yield();
            }
            return block;
        }

        public Block getBlock() {
            try {
                return get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public Block get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return null;
        }
    }

}
