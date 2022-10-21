package io.github.fisher2911.kingdoms.kingdom;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.world.KChunk;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
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

    public WorldMap getWorldMap(UUID world) {
        return this.worldMaps.computeIfAbsent(world, w -> {
            final World bukkitWorld = Bukkit.getWorld(world);
            if (bukkitWorld != null) return new WorldMap(this.plugin, world, new HashMap<>());
            return null;
        });
    }

    public ClaimedChunk getAt(Location location) {
        final Chunk chunk = location.getChunk();
        final int chunkX = chunk.getX();
        final int chunkZ = chunk.getZ();
        final UUID world = location.getWorld().getUID();
        return this.getAt(world, chunkX, chunkZ);
    }

    public ClaimedChunk getAt(UUID world, int chunkX, int chunkZ) {
        final WorldMap worldMap = this.getWorldMap(world);
        if (worldMap == null) return ClaimedChunk.wilderness(KChunk.at(world, chunkX, chunkZ));
        return worldMap.getAt(chunkX, chunkZ);
    }

    public void setChunk(ClaimedChunk chunk) {
        final WorldMap worldMap = this.getWorldMap(chunk.getWorld());
        if (worldMap == null) return;
        worldMap.setChunk(chunk);
    }

    public void setToWilderness(KChunk at) {
        final WorldMap worldMap = this.getWorldMap(at.world());
        if (worldMap == null) return;
        worldMap.setChunk(ClaimedChunk.wilderness(at));
    }

    public void setToWilderness(UUID world, int chunkX, int chunkZ) {
        final WorldMap worldMap = this.getWorldMap(world);
        if (worldMap == null) return;
        worldMap.setChunk(ClaimedChunk.wilderness(KChunk.at(world, chunkX, chunkZ)));
    }

    public void populate() {
        for (World world : Bukkit.getWorlds()) {
            this.worldMaps.put(world.getUID(), new WorldMap(this.plugin, world.getUID(), new HashMap<>()));
        }
    }
}
