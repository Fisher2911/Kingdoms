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
import io.github.fisher2911.fisherlib.world.ChunkPos;
import io.github.fisher2911.fisherlib.world.WorldPosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorldManager {

    private final Kingdoms plugin;
    private final Map<UUID, WorldMap> worldMaps;

    public WorldManager(Kingdoms plugin, Map<UUID, WorldMap> worldMaps) {
        this.plugin = plugin;
        this.worldMaps = worldMaps;
    }

    private WorldMap getWorldMap(UUID world) {
        return this.worldMaps.computeIfAbsent(world, w -> {
            final World bukkitWorld = Bukkit.getWorld(world);
            if (bukkitWorld != null) return new WorldMap(this.plugin, world, new HashMap<>());
            return null;
        });
    }

    public ClaimedChunk getAt(Location location) {
//        final Chunk chunk = location.getChunk();
//        final int chunkX = chunk.getX();
//        final int chunkZ = chunk.getZ();
//        final UUID world = location.getWorld().getUID();
//        return this.getAt(world, chunkX, chunkZ);
        return this.getAt(WorldPosition.fromLocation(location));
    }

    public ClaimedChunk getAt(WorldPosition worldPosition) {
        final int chunkX = worldPosition.position().getChunkX();
        final int chunkZ = worldPosition.position().getChunkZ();
        final UUID world = worldPosition.world();
        return this.getAt(world, chunkX, chunkZ);
    }

    public ClaimedChunk getAt(UUID world, int chunkX, int chunkZ) {
        final WorldMap worldMap = this.getWorldMap(world);
        if (worldMap == null) return ClaimedChunk.wilderness(this.plugin, ChunkPos.at(world, chunkX, chunkZ));
        return worldMap.getAt(chunkX, chunkZ);
    }

    public ClaimedChunk getAt(ChunkPos chunk) {
        return this.getAt(chunk.world(), chunk.x(), chunk.z());
    }

    public ClaimedChunk remove(UUID world, int chunkX, int chunkZ) {
        final WorldMap worldMap = this.getWorldMap(world);
        if (worldMap == null) return null;
        return worldMap.remove(chunkX, chunkZ);
    }

    public ClaimedChunk remove(ClaimedChunk chunk) {
        return this.remove(chunk.getChunk().world(), chunk.getChunk().x(), chunk.getChunk().z());
    }

    public void setChunk(ClaimedChunk chunk) {
        final WorldMap worldMap = this.getWorldMap(chunk.getWorld());
        if (worldMap == null) return;
        worldMap.setChunk(chunk);
    }

    public void setToWilderness(ChunkPos at) {
        final WorldMap worldMap = this.getWorldMap(at.world());
        if (worldMap == null) return;
        worldMap.setToWilderness(at);
    }

    public void setToWilderness(UUID world, int chunkX, int chunkZ) {
        final WorldMap worldMap = this.getWorldMap(world);
        if (worldMap == null) return;
        worldMap.setToWilderness(chunkX, chunkZ);
    }

    public boolean isChunkLoaded(ChunkPos at) {
        final WorldMap worldMap = this.getWorldMap(at.world());
        if (worldMap == null) return false;
        return worldMap.isChunkLoaded(at);
    }

    public void populate() {
        for (World world : Bukkit.getWorlds()) {
            this.worldMaps.putIfAbsent(world.getUID(), new WorldMap(this.plugin, world.getUID(), new HashMap<>()));
        }
    }

    public void saveDirty(boolean onMainThread, boolean force) {
        for (WorldMap worldMap : this.worldMaps.values()) {
            worldMap.saveDirty(onMainThread, force);
        }
    }

}
