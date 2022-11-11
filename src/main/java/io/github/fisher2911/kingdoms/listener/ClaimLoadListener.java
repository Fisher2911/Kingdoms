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

package io.github.fisher2911.kingdoms.listener;

import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.data.DataManager;
import io.github.fisher2911.kingdoms.kingdom.ClaimedChunk;
import io.github.fisher2911.kingdoms.kingdom.WorldManager;
import org.bukkit.Chunk;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.util.UUID;

public class ClaimLoadListener extends KListener {

    private final Kingdoms plugin;
    private final WorldManager worldManager;
    private final DataManager dataManager;

    public ClaimLoadListener(Kingdoms plugin) {
        super(plugin.getGlobalListener());
        this.plugin = plugin;
        this.worldManager = this.plugin.getWorldManager();
        this.dataManager = this.plugin.getDataManager();
    }

    @Override
    public void init() {
        this.globalListener.register(ChunkLoadEvent.class, this::onChunkLoad);
        this.globalListener.register(ChunkUnloadEvent.class, this::onChunkUnload);
        this.globalListener.register(WorldLoadEvent.class, this::onWorldLoad);
        this.globalListener.register(WorldUnloadEvent.class, this::onWorldUnload);
    }

    public void onWorldLoad(WorldLoadEvent event) {
        final UUID world = event.getWorld().getUID();
        for (Chunk chunk : event.getWorld().getLoadedChunks()) {
            final ClaimedChunk claimedChunk = this.worldManager.getAt(world, chunk.getX(), chunk.getZ());
            if (!claimedChunk.isWilderness()) continue;
            this.dataManager.queueChunkToLoad(claimedChunk.getChunk(), true);
        }
    }

    public void onChunkLoad(ChunkLoadEvent event) {
        final Chunk chunk = event.getChunk();
        final int chunkX = chunk.getX();
        final int chunkZ = chunk.getZ();
        final UUID world = chunk.getWorld().getUID();
        final ClaimedChunk claimedChunk = this.worldManager.getAt(world, chunkX, chunkZ);
        if (!claimedChunk.isWilderness()) return;
        this.dataManager.queueChunkToLoad(claimedChunk.getChunk(), true);
    }

    public void onChunkUnload(ChunkUnloadEvent event) {
        final Chunk chunk = event.getChunk();
        final int chunkX = chunk.getX();
        final int chunkZ = chunk.getZ();
        final UUID world = chunk.getWorld().getUID();
        final ClaimedChunk claimedChunk = this.worldManager.remove(world, chunkX, chunkZ);
        if (claimedChunk.isWilderness()) return;
        this.dataManager.queueChunkToUnload(claimedChunk.getChunk(), true);
    }

    public void onWorldUnload(WorldUnloadEvent event) {
        final UUID world = event.getWorld().getUID();
        for (Chunk chunk : event.getWorld().getLoadedChunks()) {
            final ClaimedChunk claimedChunk = this.worldManager.getAt(world, chunk.getX(), chunk.getZ());
            if (!claimedChunk.isWilderness()) continue;
            this.dataManager.queueChunkToUnload(claimedChunk.getChunk(), true);
        }
    }

}
