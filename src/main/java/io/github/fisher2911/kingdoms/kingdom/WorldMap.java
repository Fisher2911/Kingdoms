package io.github.fisher2911.kingdoms.kingdom;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.world.KChunk;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public class WorldMap {

    private final Kingdoms plugin;
    private final UUID world;
    private final Map<Long, ClaimedChunk> chunks;

    public WorldMap(Kingdoms plugin, UUID world, Map<Long, ClaimedChunk> chunks) {
        this.world = world;
        this.plugin = plugin;
        this.chunks = chunks;
    }

    @Nullable
    public ClaimedChunk getAt(long chunkKey) {
        return this.chunks.get(chunkKey);
    }

    public ClaimedChunk getAt(int x, int z) {
        return this.chunks.getOrDefault(KChunk.chunkKeyAt(x, z), ClaimedChunk.wilderness(KChunk.at(this.world, x, z)));
    }

    public void setChunk(ClaimedChunk chunk) {
        this.chunks.put(chunk.getChunk().getChunkKey(), chunk);
    }

    public void setToWilderness(int x, int z) {
        this.setChunk(ClaimedChunk.wilderness(KChunk.at(this.world, x, z)));
    }

}
