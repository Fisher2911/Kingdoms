/*
 *     Kingdoms Plugin
 *     Copyright (C) 2022  Fisher2911
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.fisher2911.kingdoms.kingdom;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.data.DataManager;
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
        return this.chunks.computeIfAbsent(KChunk.chunkKeyAt(x, z), k -> ClaimedChunk.wilderness(this.plugin, KChunk.at(this.world, x, z)));
    }

    public void setChunk(ClaimedChunk chunk) {
        final ClaimedChunk previous = this.chunks.put(chunk.getChunk().getChunkKey(), chunk);
        if (previous == null) return;
        chunk.putAllMetadata(previous.getMetadataCopy());
    }

    public void setToWilderness(int x, int z) {
        this.setToWilderness(KChunk.at(this.world, x, z));
    }

    public void setToWilderness(KChunk chunk) {
        this.setChunk(ClaimedChunk.wilderness(this.plugin, chunk));
    }

    public ClaimedChunk remove(int x, int z) {
        final ClaimedChunk chunk = this.chunks.remove(KChunk.chunkKeyAt(x, z));
        return chunk == null ? ClaimedChunk.wilderness(this.plugin, KChunk.at(this.world, x, z)) : chunk;
    }

    public boolean isChunkLoaded(KChunk chunk) {
        return this.isChunkLoaded(chunk.x(), chunk.z());
    }

    public boolean isChunkLoaded(int x, int z) {
        final long chunkKey = KChunk.chunkKeyAt(x, z);
        final ClaimedChunk claimed = this.chunks.get(chunkKey);
        if (claimed == null) return false;
        final World world = Bukkit.getWorld(this.world);
        if (world == null) return false;
        return world.isChunkLoaded(x, z);
    }

    public void saveDirty(boolean onMainThread, boolean force) {
        final DataManager dataManager = this.plugin.getDataManager();
        this.chunks.values()
                .stream()
                .filter(c -> c.isDirty() && !c.isWilderness())
                .forEach(chunk -> dataManager.queueChunkToUnload(chunk.getChunk(), !onMainThread));
        if (force) dataManager.forceSaveAllChunks(onMainThread);
    }

    public UUID getWorld() {
        return world;
    }
}
