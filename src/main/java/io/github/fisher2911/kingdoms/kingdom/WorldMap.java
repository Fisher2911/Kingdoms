package io.github.fisher2911.kingdoms.kingdom;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.world.KChunk;
import org.bukkit.Bukkit;
import org.bukkit.World;
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
        return this.chunks.getOrDefault(KChunk.chunkKeyAt(x, z), ClaimedChunk.wilderness(this.plugin, KChunk.at(this.world, x, z)));
    }

    public void setChunk(ClaimedChunk chunk) {
        if (chunk.isWilderness()) {
            this.setToWilderness(chunk.getChunk());
            return;
        }
        this.chunks.put(chunk.getChunk().getChunkKey(), chunk);
    }

    public void setToWilderness(int x, int z) {
        this.chunks.remove(KChunk.chunkKeyAt(x, z));
    }

    public void setToWilderness(KChunk chunk) {
        this.chunks.remove(chunk.getChunkKey());
    }

    public ClaimedChunk remove(int x, int z) {
        return this.chunks.remove(KChunk.chunkKeyAt(x, z));
    }

    public boolean isChunkLoaded(KChunk chunk) {
        return this.isChunkLoaded(chunk.x(), chunk.z());
    }

    public boolean isChunkLoaded(int x, int z) {
        final long chunkKey = KChunk.chunkKeyAt(x, z);
        final ClaimedChunk claimed = this.chunks.get(chunkKey);
        if (claimed.isWilderness()) return false;
        final World world = Bukkit.getWorld(this.world);
        if (world == null) return false;
        return world.isChunkLoaded(x, z);
    }

    public void saveDirty() {
        this.chunks.values()
                .stream()
                .filter(ClaimedChunk::isDirty)
                .forEach(this.plugin.getDataManager()::saveClaimedChunk);
    }

    public UUID getWorld() {
        return world;
    }
}
