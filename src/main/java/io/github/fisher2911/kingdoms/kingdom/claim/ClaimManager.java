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

package io.github.fisher2911.kingdoms.kingdom.claim;

import io.github.fisher2911.fisherlib.message.MessageHandler;
import io.github.fisher2911.fisherlib.task.TaskChain;
import io.github.fisher2911.fisherlib.world.ChunkPos;
import io.github.fisher2911.kingdoms.Kingdoms;
import io.github.fisher2911.kingdoms.api.event.chunk.ChunkClaimEvent;
import io.github.fisher2911.kingdoms.api.event.chunk.ChunkUnclaimEvent;
import io.github.fisher2911.kingdoms.kingdom.ClaimedChunk;
import io.github.fisher2911.kingdoms.kingdom.Kingdom;
import io.github.fisher2911.kingdoms.kingdom.KingdomManager;
import io.github.fisher2911.kingdoms.kingdom.WorldManager;
import io.github.fisher2911.kingdoms.kingdom.permission.KPermission;
import io.github.fisher2911.kingdoms.message.KMessage;
import io.github.fisher2911.kingdoms.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClaimManager {

    private final Kingdoms plugin;
    private final MessageHandler messageHandler;
    private final KingdomManager kingdomManager;
    private final WorldManager worldManager;
    private final Map<UUID, ClaimMode> playerClaimModes;

    public ClaimManager(Kingdoms plugin) {
        this.plugin = plugin;
        this.messageHandler = plugin.getMessageHandler();
        this.kingdomManager = this.plugin.getKingdomManager();
        this.worldManager = this.plugin.getWorldManager();
        this.playerClaimModes = new HashMap<>();
    }

    public void tryClaim(User user, Chunk chunk, boolean searchDatabase) {
        final int x = chunk.getX();
        final int z = chunk.getZ();
        this.tryClaim(user, chunk.getWorld().getUID(), x, z, searchDatabase);
    }

    public void tryClaim(User user, UUID world, int chunkX, int chunkZ, boolean searchDatabase) {
        this.kingdomManager.getKingdom(user.getKingdomId(), searchDatabase).
                ifPresentOrElse(k -> this.tryClaim(user, k, world, chunkX, chunkZ),
                        () -> this.messageHandler.sendMessage(user, KMessage.NOT_IN_KINGDOM)
                );
    }

    public void tryClaim(User user, Kingdom kingdom, UUID world, int chunkX, int chunkZ) {
        final ClaimedChunk at = this.worldManager.getAt(world, chunkX, chunkZ);
        this.tryClaim(user, kingdom, at);

    }

    public void tryClaim(User user, Kingdom kingdom, Chunk chunk) {
        final int x = chunk.getX();
        final int z = chunk.getZ();
        this.tryClaim(user, kingdom, chunk.getWorld().getUID(), x, z);
    }

    public void tryClaim(User user, Kingdom kingdom, ClaimedChunk chunk) {
        if (!this.plugin.getHooks().canClaimAt(chunk.getChunk().getCenter().toLocation())) {
            this.messageHandler.sendMessage(user, KMessage.CANNOT_CLAIM_HERE);
            return;
        }
        if (!kingdom.hasPermission(user, KPermission.CLAIM_LAND)) {
            this.messageHandler.sendMessage(user, KMessage.NO_KINGDOM_PERMISSION);
            return;
        }
        if (!chunk.isWilderness()) {
            this.messageHandler.sendMessage(user, KMessage.ALREADY_CLAIMED, chunk.getChunk());
            return;
        }
        if (kingdom.getAvailableChunks() <= 0) {
            this.messageHandler.sendMessage(user, KMessage.NO_AVAILABLE_CHUNKS);
            return;
        }
        final ClaimedChunk claimedChunk = new ClaimedChunk(
                this.plugin,
                kingdom.getId(),
                chunk.getChunk(),
                kingdom.getDefaultChunkPermissions()
        );
        claimedChunk.setDirty(true);
        final ChunkClaimEvent event = new ChunkClaimEvent(kingdom, chunk, claimedChunk, user);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        this.worldManager.setChunk(claimedChunk);
        kingdom.addClaimedChunk(claimedChunk);
        this.messageHandler.sendMessage(user, KMessage.SUCCESSFUL_CHUNK_CLAIM, claimedChunk.getChunk());
    }

    public void tryUnClaim(User user, Chunk chunk, boolean searchDatabase) {
        final int x = chunk.getX();
        final int z = chunk.getZ();
        this.tryUnClaim(user, chunk.getWorld().getUID(), x, z, searchDatabase);
    }

    public void tryUnClaim(User user, UUID world, int chunkX, int chunkZ, boolean searchDatabase) {
        this.kingdomManager.getKingdom(user.getKingdomId(), searchDatabase).
                ifPresentOrElse(k -> this.tryUnClaim(user, k, world, chunkX, chunkZ),
                        () -> this.messageHandler.sendMessage(user, KMessage.NOT_IN_KINGDOM)
                );
    }

    public void tryUnClaim(User user, Kingdom kingdom, ChunkPos chunk) {
        this.tryUnClaim(user, kingdom, chunk.world(), chunk.x(), chunk.z());
    }

    public void tryUnClaim(User user, Kingdom kingdom, UUID world, int chunkX, int chunkZ) {
        final ClaimedChunk at = this.worldManager.getAt(world, chunkX, chunkZ);
        this.tryUnClaim(user, kingdom, at);
    }

    public void tryUnClaim(User user, Kingdom kingdom, Chunk chunk) {
        final int x = chunk.getX();
        final int z = chunk.getZ();
        this.tryUnClaim(user, kingdom, chunk.getWorld().getUID(), x, z);
    }

    public void tryUnClaim(User user, Kingdom kingdom, ClaimedChunk chunk) {
        if (chunk.getKingdomId() != kingdom.getId()) {
            this.messageHandler.sendMessage(user, KMessage.NOT_CLAIMED_BY_KINGDOM, chunk.getChunk());
            return;
        }
        if (!kingdom.hasPermission(user, KPermission.UNCLAIM_LAND, chunk)) {
            this.messageHandler.sendMessage(user, KMessage.NO_KINGDOM_PERMISSION);
            return;
        }
        final ClaimedChunk claimedChunk = new ClaimedChunk(
                this.plugin,
                kingdom.getId(),
                chunk.getChunk(),
                kingdom.getDefaultChunkPermissions()
        );
        final ChunkUnclaimEvent event = new ChunkUnclaimEvent(kingdom, chunk, claimedChunk, user);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        this.worldManager.setToWilderness(claimedChunk.getChunk());
        kingdom.removeClaimedChunk(claimedChunk);
        TaskChain.create(this.plugin)
                .runAsync(() -> this.plugin.getDataManager().deleteChunk(claimedChunk.getChunk()))
                .execute();
        this.messageHandler.sendMessage(user, KMessage.SUCCESSFUL_CHUNK_UNCLAIM, claimedChunk.getChunk());
    }

    public void setClaimMode(UUID player, ClaimMode claimMode) {
        this.playerClaimModes.put(player, claimMode);
    }

    public ClaimMode getClaimMode(UUID player) {
        return this.playerClaimModes.getOrDefault(player, ClaimMode.NONE);
    }

    public void removePlayerClaimMode(UUID player) {
        this.playerClaimModes.remove(player);
    }

}
