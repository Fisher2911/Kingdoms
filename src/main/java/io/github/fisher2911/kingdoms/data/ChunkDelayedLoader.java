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

package io.github.fisher2911.kingdoms.data;

import io.github.fisher2911.fisherlib.data.DelayedLoader;
import io.github.fisher2911.fisherlib.world.ChunkPos;
import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.api.event.chunk.ClaimedChunkLoadEvent;
import io.github.fisher2911.kingdoms.api.event.chunk.ClaimedChunkUnloadEvent;
import io.github.fisher2911.kingdoms.kingdom.ClaimedChunk;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.kingdom.WildernessKingdom;
import io.github.fisher2911.kingdoms.kingdom.WorldManager;
import io.github.fisher2911.fisherlib.task.TaskChain;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class ChunkDelayedLoader implements DelayedLoader<ChunkPos> {

    private final Kingdoms plugin;
    private final Collection<ChunkPos> chunks;
    private final int sizeUntilLoad;
    private final int maxTicksUntilLoad;
    private final int interval;
    // if should save on the main thread
    private BiConsumer<Boolean, Collection<ChunkPos>> queuedConsumer;

    private BukkitTask saveTask;
    private Instant taskStartTime;

    private ChunkDelayedLoader(
            Kingdoms plugin,
            int sizeUntilLoad,
            int maxTicksUntilLoad,
            int interval,
            BiConsumer<Boolean, Collection<ChunkPos>> queuedConsumer
    ) {
        this.plugin = plugin;
        this.chunks = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.sizeUntilLoad = sizeUntilLoad;
        this.maxTicksUntilLoad = maxTicksUntilLoad;
        this.interval = interval;
        this.queuedConsumer = queuedConsumer;
    }

    public static ChunkDelayedLoader createForLoadingChunks(
            Kingdoms plugin,
            int sizeUntilLoad,
            int maxTicksUntilLoad,
            int interval
    ) {
        return new ChunkDelayedLoader(
                plugin,
                sizeUntilLoad,
                maxTicksUntilLoad,
                interval,
                (onMainThread, queued) -> {
                    if (onMainThread) {
                        handleLoadedClaimedChunks(plugin, loadClaimedChunks(plugin, plugin.getDataManager().loadClaimedChunks(queued)));
                        return;
                    }
                    TaskChain.create(plugin)
                            .supplySync(() -> plugin.getDataManager().loadClaimedChunks(queued))
                            .async(loaded -> loadClaimedChunks(plugin, loaded))
                            .consumeAsync(chunks -> handleLoadedClaimedChunks(plugin, chunks))
                            .execute();
                }
        );
    }

    public static ChunkDelayedLoader createForUnloadingChunks(
            Kingdoms plugin,
            int sizeUntilLoad,
            int maxTicksUntilLoad,
            int interval
    ) {
        return new ChunkDelayedLoader(
                plugin,
                sizeUntilLoad,
                maxTicksUntilLoad,
                interval,
                (onMainThread, queued) -> {
                    if (onMainThread) {
                        unloadClaimedChunks(plugin, queued);
                        return;
                    }
                    TaskChain.create(plugin)
                            .runAsync(() -> unloadClaimedChunks(plugin, queued))
                            .execute();
                }
        );
    }

    private static Collection<ClaimedChunk> loadClaimedChunks(Kingdoms plugin, Collection<ClaimedChunk> loaded) {
        final WorldManager worldManager = plugin.getWorldManager();
        for (final ClaimedChunk claimedChunk : loaded) {
            worldManager.setChunk(claimedChunk);
        }
        return loaded;
    }

    private static void handleLoadedClaimedChunks(Kingdoms plugin, Collection<ClaimedChunk> chunks) {
        final KingdomManager kingdomManager = plugin.getKingdomManager();
        for (ClaimedChunk chunk : chunks) {
            if (chunk.isWilderness()) continue;
            // check if kingdom is already loaded
            if (kingdomManager.getKingdom(chunk.getKingdomId(), false).isPresent()) continue;
            kingdomManager.getKingdom(chunk.getKingdomId(), true)
                    .ifPresent(kingdom -> plugin.getServer().getPluginManager().callEvent(new ClaimedChunkLoadEvent(kingdom, chunk)));
        }
    }

    private static void unloadClaimedChunks(Kingdoms plugin, Collection<ChunkPos> queued) {
        final KingdomManager kingdomManager = plugin.getKingdomManager();
        plugin.getDataManager().saveClaimedChunks(queued.stream()
                .map(plugin.getWorldManager()::getAt)
                .peek(claimedChunk -> {
                    final Kingdom kingdom = kingdomManager.getKingdom(claimedChunk.getKingdomId(), false).orElse(WildernessKingdom.INSTANCE);
                    Bukkit.getPluginManager().callEvent(new ClaimedChunkUnloadEvent(kingdom, claimedChunk));
                })
                .collect(Collectors.toSet()));
    }

    @Override
    public int getSizeUntilLoad() {
        return this.sizeUntilLoad;
    }

    @Override
    public int getMaxTicksUntilLoad() {
        return maxTicksUntilLoad;
    }

    @Override
    public void addToQueue(ChunkPos chunk, boolean startTaskIfNotRunning) {
        this.chunks.add(chunk);
        if (this.saveTask == null || this.saveTask.isCancelled() && startTaskIfNotRunning) {
            this.saveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, () -> {
                if (this.chunks.isEmpty()) {
                    this.saveTask.cancel();
                    return;
                }
                if (this.chunks.size() < this.sizeUntilLoad && Duration.between(this.taskStartTime, Instant.now()).toMillis() * 50 < this.maxTicksUntilLoad) {
                    return;
                }
                final Set<ChunkPos> copy = new HashSet<>();
                final Iterator<ChunkPos> iterator = this.chunks.iterator();
                while (iterator.hasNext()) {
                    final ChunkPos next = iterator.next();
                    copy.add(next);
                    iterator.remove();
                }
                this.queuedConsumer.accept(false, copy);
            }, this.interval, this.interval);
            this.taskStartTime = Instant.now();
        }
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Collection<ChunkPos> getToLoad() {
        return null;
    }

    @Override
    public void forceLoadAll(boolean onMainThread) {
        final Set<ChunkPos> copy = new HashSet<>();
        final Iterator<ChunkPos> iterator = this.chunks.iterator();
        while (iterator.hasNext()) {
            final ChunkPos next = iterator.next();
            copy.add(next);
            iterator.remove();
        }
        this.queuedConsumer.accept(onMainThread, copy);
    }

}
